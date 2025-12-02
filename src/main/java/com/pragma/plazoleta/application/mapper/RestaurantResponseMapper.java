package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.RestaurantListItemResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantResponseMapper {

    RestaurantResponse toResponse(Restaurant restaurant);

    RestaurantListItemResponse toListItemResponse(Restaurant restaurant);
}
