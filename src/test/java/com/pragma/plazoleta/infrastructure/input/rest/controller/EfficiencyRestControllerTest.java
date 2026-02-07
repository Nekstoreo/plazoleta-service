package com.pragma.plazoleta.infrastructure.input.rest.controller;

import com.pragma.plazoleta.application.dto.response.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.application.handler.IEfficiencyHandler;
import com.pragma.plazoleta.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EfficiencyRestControllerTest {
    private static final String EFFICIENCY_API_PATH = "/api/v1/efficiency/restaurant/{restaurantId}";
    private static final String TEST_EMAIL = "emp@test.com";

    @Mock
    private IEfficiencyHandler efficiencyHandler;

    @InjectMocks
    private EfficiencyRestController efficiencyRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(efficiencyRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Setup SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getDetails()).thenReturn(1L); // Mock Owner ID
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getOrdersEfficiencyByRestaurant_shouldReturn200() throws Exception {
        Long restaurantId = 10L;
        Long ownerId = 1L;
        OrderEfficiencyResponseDto dto = new OrderEfficiencyResponseDto(1L, 10L, 5L, TEST_EMAIL, null, null, 10L,
                "COMPLETED");

        when(efficiencyHandler.getOrdersEfficiencyByRestaurant(restaurantId, ownerId)).thenReturn(List.of(dto));

        mockMvc.perform(get(EFFICIENCY_API_PATH + "/orders", restaurantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].employeeEmail").value(TEST_EMAIL));
    }

    @Test
    void getEmployeeRankingByRestaurant_shouldReturn200() throws Exception {
        Long restaurantId = 10L;
        Long ownerId = 1L;
        EmployeeRankingResponseDto dto = new EmployeeRankingResponseDto(5L, TEST_EMAIL, 10L, 20L, 12.5, 1);

        when(efficiencyHandler.getEmployeeRankingByRestaurant(restaurantId, ownerId)).thenReturn(List.of(dto));

        mockMvc.perform(get(EFFICIENCY_API_PATH + "/employees", restaurantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(5L))
                .andExpect(jsonPath("$[0].rankingPosition").value(1));
    }

    @Test
    void getOrdersEfficiencyByRestaurant_shouldThrowException_whenNoAuthentication() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get(EFFICIENCY_API_PATH + "/orders", 10L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAuthenticatedUserId_shouldThrowException_whenNoDetails() throws Exception {
        SecurityContextHolder.clearContext();
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null);
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get(EFFICIENCY_API_PATH + "/orders", 10L))
                .andExpect(status().isInternalServerError());
    }
}
