package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantEntityMapperTest {

    private final RestaurantEntityMapper mapper = Mappers.getMapper(RestaurantEntityMapper.class);

    @Test
    void toEntity_shouldMapAllFields() {
        Restaurant r = new Restaurant("R1", "123456", "Addr", "+573001234567", "http://logo", 99L);
        r.setId(5L);

        RestaurantEntity e = mapper.toEntity(r);

        assertThat(e).isNotNull();
        assertThat(e.getId()).isEqualTo(r.getId());
        assertThat(e.getName()).isEqualTo(r.getName());
        assertThat(e.getNit()).isEqualTo(r.getNit());
        assertThat(e.getAddress()).isEqualTo(r.getAddress());
        assertThat(e.getPhone()).isEqualTo(r.getPhone());
        assertThat(e.getLogoUrl()).isEqualTo(r.getLogoUrl());
        assertThat(e.getOwnerId()).isEqualTo(r.getOwnerId());
    }

    @Test
    void toRestaurant_shouldMapAllFields() {
        RestaurantEntity e = new RestaurantEntity();
        e.setId(11L);
        e.setName("RestX");
        e.setNit("98765");
        e.setAddress("Somewhere");
        e.setPhone("+571234");
        e.setLogoUrl("logo");
        e.setOwnerId(55L);

        Restaurant r = mapper.toRestaurant(e);

        assertThat(r).isNotNull();
        assertThat(r.getId()).isEqualTo(e.getId());
        assertThat(r.getName()).isEqualTo(e.getName());
        assertThat(r.getNit()).isEqualTo(e.getNit());
        assertThat(r.getAddress()).isEqualTo(e.getAddress());
        assertThat(r.getPhone()).isEqualTo(e.getPhone());
        assertThat(r.getLogoUrl()).isEqualTo(e.getLogoUrl());
        assertThat(r.getOwnerId()).isEqualTo(e.getOwnerId());
    }

    @Test
    void toEntity_withNull_shouldReturnNullOrHandle() {
        Restaurant r = null;
        RestaurantEntity e = mapper.toEntity(r);
        assertThat(e).isNull();
    }
}
