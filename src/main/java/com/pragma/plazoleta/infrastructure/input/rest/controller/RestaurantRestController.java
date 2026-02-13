package com.pragma.plazoleta.infrastructure.input.rest.controller;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.application.dto.response.DishMenuItemResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListItemResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Restaurant management API")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantRestController {

    private final IRestaurantHandler restaurantHandler;
    private final IDishHandler dishHandler;

    @Operation(summary = "Create restaurant",
            description = "Creates a new restaurant with the provided data. Only ADMIN users can create restaurants.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Restaurant created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not authorized - requires ADMIN role",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Owner not found",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "Restaurant already exists with the provided NIT",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {
        RestaurantResponse response = restaurantHandler.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all restaurants",
            description = "Retrieves a paginated list of all restaurants ordered alphabetically by name. " +
                    "Returns only name and logo URL for each restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restaurants retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<PagedResponse<RestaurantListItemResponse>> getAllRestaurants(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of elements per page", example = "10")
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        PagedResponse<RestaurantListItemResponse> response = restaurantHandler.getAllRestaurants(page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List dishes by restaurant",
            description = "Retrieves a paginated list of active dishes from a specific restaurant. " +
                    "Results can be filtered by category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Dishes retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Restaurant not found",
                    content = @Content)
    })
    @GetMapping("/{restaurantId}/dishes")
    public ResponseEntity<PagedResponse<DishMenuItemResponseDto>> getDishesByRestaurant(
            @Parameter(description = "ID of the restaurant", required = true)
            @PathVariable(name = "restaurantId") Long restaurantId,
            @Parameter(description = "Category to filter dishes (optional)", example = "MAIN_COURSE")
            @RequestParam(name = "category", required = false) String category,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of elements per page", example = "10")
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        PagedResponse<DishMenuItemResponseDto> response = dishHandler.getDishesByRestaurant(
                restaurantId, category, page, size);
        return ResponseEntity.ok(response);
    }
}
