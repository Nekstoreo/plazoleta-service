package com.pragma.plazoleta.infrastructure.input.rest.controller;

import com.pragma.plazoleta.application.dto.response.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.application.handler.IEfficiencyHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/efficiency")
@RequiredArgsConstructor
@Tag(name = "Efficiency", description = "API para consultar la eficiencia de pedidos - Solo propietarios")
@SecurityRequirement(name = "bearerAuth")
public class EfficiencyRestController {

    private final IEfficiencyHandler efficiencyHandler;

    @Operation(summary = "Get orders efficiency by restaurant",
            description = "Retrieves the efficiency of all completed orders for a specific restaurant. " +
                    "Shows the time between order creation and completion. " +
                    "Only the restaurant owner can access this information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Orders efficiency retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderEfficiencyResponseDto.class)))),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires OWNER role and must be the restaurant owner",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Restaurant not found",
                    content = @Content)
    })
    @GetMapping("/restaurant/{restaurantId}/orders")
    public ResponseEntity<List<OrderEfficiencyResponseDto>> getOrdersEfficiencyByRestaurant(
            @Parameter(description = "ID of the restaurant", required = true)
            @PathVariable(name = "restaurantId") Long restaurantId) {
        Long ownerId = getAuthenticatedUserId();
        List<OrderEfficiencyResponseDto> response = efficiencyHandler.getOrdersEfficiencyByRestaurant(restaurantId, ownerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get employee ranking by restaurant",
            description = "Retrieves the ranking of employees by average order completion time. " +
                    "Employees are ranked from fastest (position 1) to slowest. " +
                    "Only the restaurant owner can access this information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Employee ranking retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EmployeeRankingResponseDto.class)))),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires OWNER role and must be the restaurant owner",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Restaurant not found",
                    content = @Content)
    })
    @GetMapping("/restaurant/{restaurantId}/employees")
    public ResponseEntity<List<EmployeeRankingResponseDto>> getEmployeeRankingByRestaurant(
            @Parameter(description = "ID of the restaurant", required = true)
            @PathVariable(name = "restaurantId") Long restaurantId) {
        Long ownerId = getAuthenticatedUserId();
        List<EmployeeRankingResponseDto> response = efficiencyHandler.getEmployeeRankingByRestaurant(restaurantId, ownerId);
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
