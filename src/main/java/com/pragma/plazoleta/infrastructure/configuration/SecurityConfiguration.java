package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String ROLE_EMPLOYEE = "EMPLOYEE";
    private static final String ROLE_OWNER = "OWNER";
    @Value("${app.orders.path:/api/v1/orders}")
    private String ordersPath;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurants").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/*/dishes").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/dishes").hasRole(ROLE_OWNER)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/dishes/**").hasRole(ROLE_OWNER)
                        .requestMatchers(HttpMethod.GET, "/api/v1/efficiency/**").hasRole(ROLE_OWNER)
                        .requestMatchers(HttpMethod.POST, ordersPath).hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, ordersPath).hasRole(ROLE_EMPLOYEE)
                        .requestMatchers(HttpMethod.PUT, ordersPath).hasRole(ROLE_EMPLOYEE)
                        .requestMatchers(HttpMethod.PATCH, ordersPath + "/**").hasRole(ROLE_EMPLOYEE)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
