package com.pragma.plazoleta.infrastructure.input.rest.controller;

import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management API for clients")
@SecurityRequirement(name = "bearerAuth")
public class OrderRestController {

    private final IOrderHandler orderHandler;

    public OrderRestController(IOrderHandler orderHandler) {
        this.orderHandler = orderHandler;
    }

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
