package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.infrastructure.security.JwtAuthenticationFilter;
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

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger and API docs - public
                        .requestMatchers(
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Restaurants - only ADMIN can create
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurants").hasRole("ADMIN")
                        // Restaurants - any authenticated user can list restaurants and dishes
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/*/dishes").authenticated()
                        // Dishes - only OWNER can create/update
                        .requestMatchers(HttpMethod.POST, "/api/v1/dishes").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/dishes/**").hasRole("OWNER")
                        // Orders - only CLIENT can create orders
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("CLIENT")
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
