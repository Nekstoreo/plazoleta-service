package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.infrastructure.constant.ApiConstants;
import com.pragma.plazoleta.infrastructure.constant.SecurityConstants;
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
                                ApiConstants.API_DOCS_PATH + "/**",
                                "/swagger-ui/**",
                                ApiConstants.SWAGGER_PATH
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiConstants.RESTAURANTS_BASE_PATH).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, ApiConstants.RESTAURANTS_BASE_PATH).authenticated()
                        .requestMatchers(HttpMethod.GET, ApiConstants.RESTAURANTS_BASE_PATH + "/*/dishes").authenticated()
                        .requestMatchers(HttpMethod.POST, ApiConstants.DISHES_BASE_PATH).hasRole(SecurityConstants.ROLE_OWNER)
                        .requestMatchers(HttpMethod.PATCH, ApiConstants.DISHES_BASE_PATH + "/**").hasRole(SecurityConstants.ROLE_OWNER)
                        .requestMatchers(HttpMethod.GET, ApiConstants.EFFICIENCY_BASE_PATH + "/**").hasRole(SecurityConstants.ROLE_OWNER)
                        .requestMatchers(HttpMethod.POST, ordersPath).hasRole(SecurityConstants.ROLE_CLIENT)
                        .requestMatchers(HttpMethod.GET, ordersPath).hasRole(SecurityConstants.ROLE_EMPLOYEE)
                        .requestMatchers(HttpMethod.PUT, ordersPath).hasRole(SecurityConstants.ROLE_EMPLOYEE)
                        .requestMatchers(HttpMethod.PATCH, ordersPath + "/**").hasRole(SecurityConstants.ROLE_EMPLOYEE)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
