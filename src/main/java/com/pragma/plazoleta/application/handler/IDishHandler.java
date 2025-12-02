package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishActiveRequestDto;
import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishUpdateRequestDto;
import com.pragma.plazoleta.application.dto.response.DishMenuItemResponseDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;

public interface IDishHandler {

    DishResponseDto createDish(DishRequestDto dishRequestDto, Long ownerId);

    DishResponseDto updateDish(Long dishId, DishUpdateRequestDto updateRequestDto, Long ownerId);

    DishResponseDto changeDishActiveStatus(Long dishId, DishActiveRequestDto requestDto, Long ownerId);

    PagedResponse<DishMenuItemResponseDto> getDishesByRestaurant(Long restaurantId, String category, int page, int size);
}
