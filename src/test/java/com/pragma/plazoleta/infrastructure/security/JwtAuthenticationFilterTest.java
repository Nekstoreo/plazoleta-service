package com.pragma.plazoleta.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TEST_TOKEN = "token123";
    private static final String TEST_EMAIL = "user@example.com";
    private static final String ROLE_CLIENT = "CLIENT";

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_noHeader_callsChainAndNoAuth() throws Exception {
        JwtTokenValidator validator = mock(JwtTokenValidator.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(validator);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void doFilter_withValidToken_setsAuthentication() throws Exception {
        JwtTokenValidator validator = mock(JwtTokenValidator.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(validator);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + TEST_TOKEN);

        when(validator.isTokenValid(TEST_TOKEN)).thenReturn(true);
        when(validator.extractEmail(TEST_TOKEN)).thenReturn(TEST_EMAIL);
        when(validator.extractRole(TEST_TOKEN)).thenReturn(ROLE_CLIENT);
        when(validator.extractUserId(TEST_TOKEN)).thenReturn(42L);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(TEST_EMAIL, auth.getPrincipal());
        assertEquals(42L, auth.getDetails());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + ROLE_CLIENT)));
    }

    @Test
    void doFilter_withInvalidToken_doesNotSetAuthentication() throws Exception {
        JwtTokenValidator validator = mock(JwtTokenValidator.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(validator);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String token = TEST_TOKEN;
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        when(validator.isTokenValid(token)).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
