package com.pragma.plazoleta.infrastructure.input.rest.controller;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurants", description = "Restaurant management API")
public class RestaurantRestController {

    private final IRestaurantHandler restaurantHandler;

    public RestaurantRestController(IRestaurantHandler restaurantHandler) {
        this.restaurantHandler = restaurantHandler;
    }

    @Operation(summary = "Create restaurant",
            description = "Creates a new restaurant with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Restaurant created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data",
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
}
