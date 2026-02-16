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
class ClientInfoFeignAdapterTest {

    private static final Long CLIENT_ID = 10L;

    @Mock
    private IUserFeignClient userFeignClient;

    @InjectMocks
    private ClientInfoFeignAdapter adapter;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(CLIENT_ID);
        userDto.setPhone("+573001234567");
        userDto.setEmail("client@test.com");
    }

    @Test
    void getClientPhoneById_ShouldReturnPhoneWhenUserExists() {
        when(userFeignClient.getUserById(CLIENT_ID)).thenReturn(Optional.of(userDto));

        Optional<String> result = adapter.getClientPhoneById(CLIENT_ID);

        assertTrue(result.isPresent());
        assertEquals("+573001234567", result.get());
    }

    @Test
    void getClientPhoneById_ShouldReturnEmptyWhenFeignFails() {
        when(userFeignClient.getUserById(CLIENT_ID)).thenThrow(notFoundException());

        Optional<String> result = adapter.getClientPhoneById(CLIENT_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void getClientEmailById_ShouldReturnEmailWhenUserExists() {
        when(userFeignClient.getUserById(CLIENT_ID)).thenReturn(Optional.of(userDto));

        Optional<String> result = adapter.getClientEmailById(CLIENT_ID);

        assertTrue(result.isPresent());
        assertEquals("client@test.com", result.get());
    }

    @Test
    void getClientEmailById_ShouldReturnEmptyWhenFeignFails() {
        when(userFeignClient.getUserById(CLIENT_ID)).thenThrow(notFoundException());

        Optional<String> result = adapter.getClientEmailById(CLIENT_ID);

        assertTrue(result.isEmpty());
    }

    private FeignException notFoundException() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/api/v1/users/10",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        return new FeignException.NotFound("not found", request, null, null);
    }
}
