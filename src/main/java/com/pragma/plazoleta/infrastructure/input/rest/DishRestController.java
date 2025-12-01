package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.DishActiveRequestDto;
import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishUpdateRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dishes")
@Tag(name = "Dishes", description = "Dish management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DishRestController {

    private final IDishHandler dishHandler;

    public DishRestController(IDishHandler dishHandler) {
        this.dishHandler = dishHandler;
    }

    @Operation(summary = "Create a new dish",
            description = "Creates a new dish for a restaurant. Only the restaurant owner can create dishes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DishResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not the restaurant owner",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<DishResponseDto> createDish(
            @Valid @RequestBody DishRequestDto dishRequestDto) {
        Long ownerId = getAuthenticatedUserId();
        DishResponseDto createdDish = dishHandler.createDish(dishRequestDto, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
    }

    @Operation(summary = "Update a dish",
            description = "Updates price and description of an existing dish. Only the restaurant owner can update dishes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DishResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not the restaurant owner",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Dish not found",
                    content = @Content)
    })
    @PatchMapping("/{dishId}")
    public ResponseEntity<DishResponseDto> updateDish(
            @Parameter(description = "ID of the dish to update", required = true)
            @PathVariable Long dishId,
            @Valid @RequestBody DishUpdateRequestDto updateRequestDto) {
        Long ownerId = getAuthenticatedUserId();
        DishResponseDto updatedDish = dishHandler.updateDish(dishId, updateRequestDto, ownerId);
        return ResponseEntity.ok(updatedDish);
    }

    @Operation(summary = "Activate or deactivate a dish",
            description = "Allows the restaurant owner to enable or disable a dish without modifying other details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DishResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not the restaurant owner",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Dish not found",
                    content = @Content)
    })
    @PatchMapping("/{dishId}/status")
    public ResponseEntity<DishResponseDto> changeDishActiveStatus(
            @Parameter(description = "ID of the dish to toggle", required = true)
            @PathVariable Long dishId,
            @Valid @RequestBody DishActiveRequestDto activeRequestDto) {
        Long ownerId = getAuthenticatedUserId();
        DishResponseDto updatedDish = dishHandler.changeDishActiveStatus(dishId, activeRequestDto, ownerId);
        return ResponseEntity.ok(updatedDish);
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
