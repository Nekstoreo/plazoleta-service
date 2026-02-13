package com.pragma.plazoleta.infrastructure.constant;

public final class SecurityConstants {

    private SecurityConstants() {
        throw new AssertionError("Cannot instantiate SecurityConstants");
    }

    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    public static final String ROLE_CLIENT = "CLIENT";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String JWT_HEADER = "Authorization";
    public static final String BEARER_AUTH_SCHEME = "bearer";
    public static final String JWT_TOKEN_TYPE = "JWT";

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String SPRING_SECURITY_CONTEXT_HOLDER = "org.springframework.security.context.SecurityContextHolder";
}
