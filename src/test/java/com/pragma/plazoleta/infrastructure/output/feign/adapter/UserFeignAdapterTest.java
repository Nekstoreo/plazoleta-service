package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.infrastructure.output.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.dto.UserDto;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFeignAdapterTest {

    private static final Long USER_ID = 1L;
    private static final String OWNER_ROLE = "OWNER";

    @Mock
    private IUserFeignClient userFeignClient;

    @InjectMocks
    private UserFeignAdapter userFeignAdapter;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName("Juan");
        userDto.setLastName("Perez");
        userDto.setEmail("juan.perez@email.com");
        userDto.setRole(OWNER_ROLE);
    }

    @Nested
    @DisplayName("Exists By ID Tests")
    class ExistsByIdTests {

        @Test
        @DisplayName("Should return true when user exists")
        void shouldReturnTrueWhenUserExists() {
            // Arrange
            when(userFeignClient.getUserById(USER_ID)).thenReturn(Optional.of(userDto));

            // Act
            boolean result = userFeignAdapter.existsById(USER_ID);

            // Assert
            assertTrue(result);
            verify(userFeignClient).getUserById(USER_ID);
        }

        @Test
        @DisplayName("Should return false when user does not exist")
        void shouldReturnFalseWhenUserDoesNotExist() {
            // Arrange
            when(userFeignClient.getUserById(USER_ID)).thenReturn(Optional.empty());

            // Act
            boolean result = userFeignAdapter.existsById(USER_ID);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when feign throws NotFound exception")
        void shouldReturnFalseWhenFeignThrowsNotFoundException() {
            // Arrange
            Request request = Request.create(
                    Request.HttpMethod.GET,
                    "/api/v1/users/1",
                    Collections.emptyMap(),
                    null,
                    new RequestTemplate()
            );
            when(userFeignClient.getUserById(USER_ID))
                    .thenThrow(new FeignException.NotFound("", request, null, null));

            // Act
            boolean result = userFeignAdapter.existsById(USER_ID);

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Get User Role By ID Tests")
    class GetUserRoleByIdTests {

        @Test
        @DisplayName("Should return role when user exists")
        void shouldReturnRoleWhenUserExists() {
            // Arrange
            when(userFeignClient.getUserById(USER_ID)).thenReturn(Optional.of(userDto));

            // Act
            Optional<String> result = userFeignAdapter.getUserRoleById(USER_ID);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(OWNER_ROLE, result.get());
            verify(userFeignClient).getUserById(USER_ID);
        }

        @Test
        @DisplayName("Should return empty when user does not exist")
        void shouldReturnEmptyWhenUserDoesNotExist() {
            // Arrange
            when(userFeignClient.getUserById(USER_ID)).thenReturn(Optional.empty());

            // Act
            Optional<String> result = userFeignAdapter.getUserRoleById(USER_ID);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when feign throws NotFound exception")
        void shouldReturnEmptyWhenFeignThrowsNotFoundException() {
            // Arrange
            Request request = Request.create(
                    Request.HttpMethod.GET,
                    "/api/v1/users/1",
                    Collections.emptyMap(),
                    null,
                    new RequestTemplate()
            );
            when(userFeignClient.getUserById(USER_ID))
                    .thenThrow(new FeignException.NotFound("", request, null, null));

            // Act
            Optional<String> result = userFeignAdapter.getUserRoleById(USER_ID);

            // Assert
            assertTrue(result.isEmpty());
        }
    }
}
