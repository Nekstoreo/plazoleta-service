package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishUpdateRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;

public interface IDishHandler {

    DishResponseDto createDish(DishRequestDto dishRequestDto, Long ownerId);

    DishResponseDto updateDish(Long dishId, DishUpdateRequestDto updateRequestDto, Long ownerId);
}
