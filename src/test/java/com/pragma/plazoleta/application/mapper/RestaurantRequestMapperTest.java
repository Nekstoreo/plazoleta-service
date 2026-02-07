package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.CreateRestaurantRequest;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantRequestMapperTest {

    private final RestaurantRequestMapper mapper = Mappers.getMapper(RestaurantRequestMapper.class);

    @Test
    void toRestaurant_shouldMapFields() {
        CreateRestaurantRequest req = new CreateRestaurantRequest("R1", "12345", "Addr", "+573001234567", "http://logo", 77L);

        Restaurant r = mapper.toRestaurant(req);

        assertThat(r).isNotNull();
        assertThat(r.getName()).isEqualTo(req.name());
        assertThat(r.getNit()).isEqualTo(req.nit());
        assertThat(r.getAddress()).isEqualTo(req.address());
        assertThat(r.getPhone()).isEqualTo(req.phone());
        assertThat(r.getLogoUrl()).isEqualTo(req.logoUrl());
        assertThat(r.getOwnerId()).isEqualTo(req.ownerId());
    }

    @Test
    void toRestaurant_shouldReturnNull_whenRequestIsNull() {
        assertThat(mapper.toRestaurant(null)).isNull();
    }
}
