package com.pragma.plazoleta.infrastructure.input.rest.controller;

import com.pragma.plazoleta.application.dto.request.AssignOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.DeliverOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.MarkOrderReadyRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.infrastructure.constant.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pragma.plazoleta.application.dto.response.TraceabilityResponseDto;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.ORDERS_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management API for clients and employees")
@SecurityRequirement(name = "bearerAuth")
public class OrderRestController {

    private final IOrderHandler orderHandler;

    @Operation(summary = "Create a new order",
            description = "Creates a new order for the authenticated client. " +
                    "The order must contain at least one dish from the specified restaurant. " +
                    "A client can only have one active order at a time (pending, in preparation, or ready).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data - Empty order, invalid quantity, or dish not from restaurant",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires CLIENT role",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Restaurant or dish not found",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "Client already has an active order in progress",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody CreateOrderRequestDto request) {
        Long clientId = getAuthenticatedUserId();
        OrderResponseDto response = orderHandler.createOrder(request, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List orders by status",
            description = "Retrieves a paginated list of orders filtered by status. " +
                    "Only employees can access this endpoint. " +
                    "Orders are filtered by the restaurant associated with the employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid status value",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires EMPLOYEE role",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Employee not associated with any restaurant",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponseDto>> getOrdersByStatus(
            @Parameter(description = "Order status to filter by", 
                    required = true, 
                    example = "PENDING",
                    schema = @Schema(allowableValues = {"PENDING", "IN_PREPARATION", "READY", "DELIVERED", "CANCELLED"}))
            @RequestParam(name = "status") String status,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of elements per page", example = "10")
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Long employeeId = getAuthenticatedUserId();
        PagedResponse<OrderResponseDto> response = orderHandler.getOrdersByStatus(employeeId, status, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Assign order to employee and change status to IN_PREPARATION",
            description = "Allows an employee to assign an order to themselves and change its status from PENDING to IN_PREPARATION. " +
                    "Only PENDING orders from the employee's restaurant can be assigned.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Order assigned successfully and status updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data or invalid order status (only PENDING orders can be assigned)",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires EMPLOYEE role",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Order not found or employee not associated with any restaurant",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "Order does not belong to the employee's restaurant",
                    content = @Content)
    })
    @PutMapping
    public ResponseEntity<OrderResponseDto> assignOrderToEmployee(
            @Valid @RequestBody AssignOrderRequestDto request) {
        Long employeeId = getAuthenticatedUserId();
        OrderResponseDto response = orderHandler.assignOrderToEmployee(request, employeeId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mark order as ready and notify client",
            description = "Marks an order as READY and sends a WhatsApp notification to the client with a security PIN. " +
                    "Only orders in IN_PREPARATION status can be marked as ready. " +
                    "The client must present this PIN to claim their order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Order marked as ready and notification sent to client",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data or order is not in IN_PREPARATION status",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires EMPLOYEE role",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Order not found, employee not associated with restaurant, or client phone not found",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "Order does not belong to the employee's restaurant",
                    content = @Content)
    })
    @PatchMapping("/ready")
    public ResponseEntity<OrderResponseDto> markOrderAsReady(
            @Valid @RequestBody MarkOrderReadyRequestDto request) {
        Long employeeId = getAuthenticatedUserId();
        OrderResponseDto response = orderHandler.markOrderAsReady(request, employeeId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mark order as delivered",
            description = "Marks an order as DELIVERED. " +
                    "Only orders in READY status can be marked as delivered. " +
                    "The employee must provide the security PIN sent to the client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Order marked as delivered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data, invalid order status, or invalid security PIN",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires EMPLOYEE role",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Order not found or employee not associated with restaurant",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "Order does not belong to the employee's restaurant",
                    content = @Content)
    })
    @PatchMapping("/deliver")
    public ResponseEntity<OrderResponseDto> deliverOrder(
            @Valid @RequestBody DeliverOrderRequestDto request) {
        Long employeeId = getAuthenticatedUserId();
        OrderResponseDto response = orderHandler.deliverOrder(request, employeeId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancel an order",
            description = "Cancels an order. " +
                    "Only the client who created the order can cancel it. " +
                    "Only orders in PENDING status can be cancelled.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Order cancelled successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Order is not in PENDING status",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires CLIENT role or user is not the owner",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Order not found",
                    content = @Content)
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable(name = "orderId") Long orderId) {
        Long clientId = getAuthenticatedUserId();
        OrderResponseDto response = orderHandler.cancelOrder(orderId, clientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get order traceability",
            description = "Retrieves the history of status changes for a specific order. " +
                    "Only the client who created the order can access its traceability.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Traceability retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraceabilityResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires CLIENT role or user is not the owner",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Order not found",
                    content = @Content)
    })
    @GetMapping("/{orderId}/traceability")
    public ResponseEntity<List<TraceabilityResponseDto>> getTraceabilityByOrderId(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable(name = "orderId") Long orderId) {
        Long clientId = getAuthenticatedUserId();
        List<TraceabilityResponseDto> response = orderHandler.getTraceabilityByOrderId(orderId, clientId);
        return ResponseEntity.ok(response);
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is not available in the security context");
        }
        Object details = authentication.getDetails();
        if (details == null) {
            throw new IllegalStateException("User details are not available in authentication");
        }
        return (Long) details;
    }
}
