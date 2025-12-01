package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.model.Dish;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
