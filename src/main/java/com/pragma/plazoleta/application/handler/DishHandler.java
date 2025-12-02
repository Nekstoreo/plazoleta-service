package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishActiveRequestDto;
import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishUpdateRequestDto;
import com.pragma.plazoleta.application.dto.response.DishMenuItemResponseDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.PagedResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final DishDtoMapper dishDtoMapper;

    public DishHandler(IDishServicePort dishServicePort, DishDtoMapper dishDtoMapper) {
        this.dishServicePort = dishServicePort;
        this.dishDtoMapper = dishDtoMapper;
    }

    @Override
    public DishResponseDto createDish(DishRequestDto dishRequestDto, Long ownerId) {
        Dish dish = dishDtoMapper.toDish(dishRequestDto);
        Dish savedDish = dishServicePort.createDish(dish, ownerId);
        return dishDtoMapper.toDishResponseDto(savedDish);
    }

    @Override
    public DishResponseDto updateDish(Long dishId, DishUpdateRequestDto updateRequestDto, Long ownerId) {
        Dish updatedDish = dishServicePort.updateDish(
                dishId,
                updateRequestDto.getPrice(),
                updateRequestDto.getDescription(),
                ownerId
        );
        return dishDtoMapper.toDishResponseDto(updatedDish);
    }

    @Override
    public DishResponseDto changeDishActiveStatus(Long dishId, DishActiveRequestDto requestDto, Long ownerId) {
        Dish updatedDish = dishServicePort.changeDishActiveStatus(
                dishId,
                requestDto.getActive(),
                ownerId
        );
        return dishDtoMapper.toDishResponseDto(updatedDish);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DishMenuItemResponseDto> getDishesByRestaurant(
            Long restaurantId, String category, int page, int size) {
        PagedResult<Dish> pagedResult = dishServicePort.getDishesByRestaurant(
                restaurantId, category, page, size);

        List<DishMenuItemResponseDto> dishes = pagedResult.getContent().stream()
                .map(dishDtoMapper::toDishMenuItemResponseDto)
                .toList();

        return PagedResponse.<DishMenuItemResponseDto>builder()
                .content(dishes)
                .page(pagedResult.getPage())
                .size(pagedResult.getSize())
                .totalElements(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .first(pagedResult.isFirst())
                .last(pagedResult.isLast())
                .build();
    }
}
