package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.dto.UserDto;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeRestaurantFeignAdapterTest {

    private static final Long EMPLOYEE_ID = 20L;

    @Mock
    private IUserFeignClient userFeignClient;

    @InjectMocks
    private EmployeeRestaurantFeignAdapter adapter;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(EMPLOYEE_ID);
        userDto.setEmail("employee@test.com");
        userDto.setRestaurantId(99L);
    }

    @Test
    void getRestaurantIdByEmployeeId_ShouldReturnRestaurantIdWhenUserExists() {
        when(userFeignClient.getUserById(EMPLOYEE_ID)).thenReturn(Optional.of(userDto));

        Optional<Long> result = adapter.getRestaurantIdByEmployeeId(EMPLOYEE_ID);

        assertTrue(result.isPresent());
        assertEquals(99L, result.get());
    }

    @Test
    void getRestaurantIdByEmployeeId_ShouldReturnEmptyWhenFeignFails() {
        when(userFeignClient.getUserById(EMPLOYEE_ID)).thenThrow(notFoundException());

        Optional<Long> result = adapter.getRestaurantIdByEmployeeId(EMPLOYEE_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeeEmailById_ShouldReturnEmailWhenUserExists() {
        when(userFeignClient.getUserById(EMPLOYEE_ID)).thenReturn(Optional.of(userDto));

        Optional<String> result = adapter.getEmployeeEmailById(EMPLOYEE_ID);

        assertTrue(result.isPresent());
        assertEquals("employee@test.com", result.get());
    }

    @Test
    void getEmployeeEmailById_ShouldReturnEmptyWhenFeignFails() {
        when(userFeignClient.getUserById(EMPLOYEE_ID)).thenThrow(notFoundException());

        Optional<String> result = adapter.getEmployeeEmailById(EMPLOYEE_ID);

        assertTrue(result.isEmpty());
    }

    private FeignException notFoundException() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/api/v1/users/20",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        return new FeignException.NotFound("not found", request, null, null);
    }
}
