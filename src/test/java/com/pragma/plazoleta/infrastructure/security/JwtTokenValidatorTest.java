package com.pragma.plazoleta.infrastructure.security;

import com.pragma.plazoleta.infrastructure.constant.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenValidatorTest {

    private static final String SECRET = "01234567890123456789012345678901"; // 32 chars
    private static final String TEST_EMAIL = "user@example.com";
    private static final Long TEST_USER_ID = 42L;

    @Test
    void validToken_extractsClaimsAndIsValid() {
        var key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .subject(TEST_EMAIL)
                .claim("role", SecurityConstants.ROLE_CLIENT)
                .claim("userId", TEST_USER_ID)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();

        JwtTokenValidator validator = new JwtTokenValidator(SECRET);

        assertEquals(TEST_EMAIL, validator.extractEmail(token));
        assertEquals(SecurityConstants.ROLE_CLIENT, validator.extractRole(token));
        assertEquals(TEST_USER_ID, validator.extractUserId(token));
        assertTrue(validator.isTokenValid(token));
    }

    @Test
    void expiredToken_returnsNullsAndIsInvalid() {
        var key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .subject(TEST_EMAIL)
                .claim("role", SecurityConstants.ROLE_CLIENT)
                .claim("userId", TEST_USER_ID)
                .expiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(key)
                .compact();

        JwtTokenValidator validator = new JwtTokenValidator(SECRET);

        assertNull(validator.extractEmail(token));
        assertNull(validator.extractRole(token));
        assertNull(validator.extractUserId(token));
        assertFalse(validator.isTokenValid(token));
    }

    @Test
    void malformedToken_returnsNullsAndIsInvalid() {
        JwtTokenValidator validator = new JwtTokenValidator(SECRET);

        String bad = "not-a-token";

        assertNull(validator.extractEmail(bad));
        assertNull(validator.extractRole(bad));
        assertNull(validator.extractUserId(bad));
        assertFalse(validator.isTokenValid(bad));
    }
}
