package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.RestaurantListItemResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantResponseMapperTest {

    private final RestaurantResponseMapper mapper = Mappers.getMapper(RestaurantResponseMapper.class);

    @Test
    void toResponse_shouldMapAllFields() {
        Restaurant restaurant = new Restaurant("Restaurante A", "123456789", "Calle 10", "+573001234567", "http://logo.com", 1L);
        restaurant.setId(10L);

        RestaurantResponse response = mapper.toResponse(restaurant);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(restaurant.getId());
        assertThat(response.getName()).isEqualTo(restaurant.getName());
        assertThat(response.getNit()).isEqualTo(restaurant.getNit());
        assertThat(response.getAddress()).isEqualTo(restaurant.getAddress());
        assertThat(response.getPhone()).isEqualTo(restaurant.getPhone());
        assertThat(response.getLogoUrl()).isEqualTo(restaurant.getLogoUrl());
        assertThat(response.getOwnerId()).isEqualTo(restaurant.getOwnerId());
    }

    @Test
    void toListItemResponse_shouldMapNameAndLogo() {
        Restaurant restaurant = new Restaurant("Restaurante B", "987654321", "Calle 20", "+573007654321", "http://logoB.com", 2L);

        RestaurantListItemResponse response = mapper.toListItemResponse(restaurant);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(restaurant.getName());
        assertThat(response.getLogoUrl()).isEqualTo(restaurant.getLogoUrl());
    }

    @Test
    void toResponse_shouldReturnNull_whenRestaurantIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toListItemResponse_shouldReturnNull_whenRestaurantIsNull() {
        assertThat(mapper.toListItemResponse(null)).isNull();
    }
}
