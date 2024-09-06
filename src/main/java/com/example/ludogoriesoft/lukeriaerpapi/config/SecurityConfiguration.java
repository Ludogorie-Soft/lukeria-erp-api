package com.example.ludogoriesoft.lukeriaerpapi.config;


import com.example.ludogoriesoft.lukeriaerpapi.handler.JwtAuthenticationEntryPoint;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.example.ludogoriesoft.lukeriaerpapi.enums.Role.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper))
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/v1/images/uploadImageForPackage",
                        "/api/v1/images/**",
                        "/api/v1/auth/**")
                .permitAll()
                .requestMatchers("/api/v1/user/me").permitAll()
                .requestMatchers("/api/v1/user/**").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/client/**").hasAnyRole(ADMIN.name(), PRODUCTION_MANAGER.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/client", "/api/v1/upload/file").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/client/**").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/client/**").hasAnyRole(ADMIN.name())


                .requestMatchers(HttpMethod.GET, "/api/v1/package/**").hasAnyRole(TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name(), ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/package").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/package/**").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/package/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/plate/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(),TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/plate").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/plate/**").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/plate/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/carton/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(),TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/carton").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/carton/**").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/carton/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/product/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/product").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/product/produce/***").hasAnyRole(ADMIN.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/product/**").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/product/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/order/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/order").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/order/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/order/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/orderProduct/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/orderProduct/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/orderProduct/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/orderProduct/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/monthlyOrder/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/monthlyOrder").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/monthlyOrder/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/monthlyOrder/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/monthlyOrderProduct/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/monthlyOrderProduct").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/monthlyOrderProduct/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/monthlyOrderProduct/**").hasAnyRole(ADMIN.name())

                .requestMatchers("/api/v1/material-order/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/invoice/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/invoice/number/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/invoice/**").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/invoice/**").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/invoice/**").hasAnyRole(ADMIN.name())
                .requestMatchers("/api/v1/invoiceOrderProduct/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.POST,"/api/v1/upload/**").hasAnyRole(ADMIN.name())
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());

        return http.build();
    }
}
