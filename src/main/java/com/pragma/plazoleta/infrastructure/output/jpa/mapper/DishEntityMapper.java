package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DishEntityMapper {

    @Mapping(target = "restaurant.id", source = "restaurantId")
    DishEntity toEntity(Dish dish);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    Dish toDish(DishEntity dishEntity);
}
