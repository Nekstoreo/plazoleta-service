package com.pragma.plazoleta.infrastructure.input.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderItemRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PagedResponse;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.infrastructure.exception.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderRestController Tests")
class OrderRestControllerTest {
    private static final String ORDERS_API_PATH = "/api/v1/orders";
    private static final String ROLE_EMPLOYEE = "EMPLOYEE";
    private static final String ROLE_CLIENT = "CLIENT";
    private static final String STATUS_PENDING = "PENDING";
    private static final String TEST_USER = "user";

    @Mock
    private IOrderHandler orderHandler;

    @InjectMocks
    private OrderRestController orderRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthenticationWithId(Long id, String role) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                TEST_USER, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        auth.setDetails(id);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {
        setAuthenticationWithId(11L, ROLE_CLIENT);

        CreateOrderRequestDto request = new CreateOrderRequestDto();
        request.setRestaurantId(1L);
        request.setItems(List.of(new OrderItemRequestDto(1L, 2)));

        OrderResponseDto response = new OrderResponseDto();
        response.setId(100L);
        response.setRestaurantId(1L);

        given(orderHandler.createOrder(any(CreateOrderRequestDto.class), eq(11L))).willReturn(response);

        mockMvc.perform(post(ORDERS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.restaurantId").value(1));
    }

    @Test
    void getOrdersByStatus_shouldReturnPagedOrders() throws Exception {
        setAuthenticationWithId(22L, ROLE_EMPLOYEE);

        PagedResponse<OrderResponseDto> paged = new PagedResponse<>();
        paged.setContent(List.of(new OrderResponseDto()));
        paged.setPage(0);
        paged.setSize(1);
        paged.setTotalElements(1);

        given(orderHandler.getOrdersByStatus(eq(22L), eq(STATUS_PENDING), eq(0), eq(10))).willReturn(paged);

        mockMvc.perform(get(ORDERS_API_PATH)
                .param("status", STATUS_PENDING)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void cancelOrder_shouldReturnOk() throws Exception {
        setAuthenticationWithId(33L, ROLE_CLIENT);

        var response = new OrderResponseDto();
        response.setId(55L);
        response.setStatus("CANCELLED");

        given(orderHandler.cancelOrder(eq(55L), eq(33L))).willReturn(response);

        mockMvc.perform(post(ORDERS_API_PATH + "/55/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(55))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getTraceabilityByOrderId_shouldReturnList() throws Exception {
        setAuthenticationWithId(44L, ROLE_CLIENT);

        given(orderHandler.getTraceabilityByOrderId(eq(77L), eq(44L))).willReturn(List.of());

        mockMvc.perform(get(ORDERS_API_PATH + "/77/traceability"))
                .andExpect(status().isOk());
    }

    @Test
    void assignOrderToEmployee_shouldReturnOk() throws Exception {
        setAuthenticationWithId(22L, ROLE_EMPLOYEE);
        var request = new com.pragma.plazoleta.application.dto.request.AssignOrderRequestDto(100L);
        var response = new OrderResponseDto();
        response.setId(100L);

        given(orderHandler.assignOrderToEmployee(any(), eq(22L))).willReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(ORDERS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void markOrderAsReady_shouldReturnOk() throws Exception {
        setAuthenticationWithId(22L, ROLE_EMPLOYEE);
        var request = new com.pragma.plazoleta.application.dto.request.MarkOrderReadyRequestDto(100L);
        var response = new OrderResponseDto();

        given(orderHandler.markOrderAsReady(any(), eq(22L))).willReturn(response);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(ORDERS_API_PATH + "/ready")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deliverOrder_shouldReturnOk() throws Exception {
        setAuthenticationWithId(22L, ROLE_EMPLOYEE);
        var request = new com.pragma.plazoleta.application.dto.request.DeliverOrderRequestDto(100L, "123456");
        var response = new OrderResponseDto();

        given(orderHandler.deliverOrder(any(), eq(22L))).willReturn(response);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(ORDERS_API_PATH + "/deliver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getAuthenticatedUserId_shouldThrowException_whenNoAuthentication() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/v1/orders")
                .param("status", "PENDING"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAuthenticatedUserId_shouldThrowException_whenNoDetails() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(TEST_USER, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get(ORDERS_API_PATH)
                .param("status", STATUS_PENDING))
                .andExpect(status().isInternalServerError());
    }
}
