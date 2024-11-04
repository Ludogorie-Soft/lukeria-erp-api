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
    public static final String CLIENT_URL = "/api/v1/client/**";
    public static final String PACKAGE_URL = "/api/v1/package/**";
    public static final String PLATE_URL = "/api/v1/plate/**";
    public static final String CARTON_URL = "/api/v1/carton/**";
    public static final String PRODUCT_URL = "/api/v1/product/**";
    public static final String ORDER_URL = "/api/v1/order/**";
    public static final String ORDER_PRODUCT_URL = "/api/v1/orderProduct/**";
    public static final String MONTHLY_ORDER_URL = "/api/v1/monthlyOrder/**";
    public static final String MONTHLY_ORDER_PRODUCT_URL = "/api/v1/monthlyOrderProduct/**";
    public static final String INVOICE_URL = "/api/v1/monthlyOrderProduct/**";
    public static final String CLIENT_USER_URL = "/api/v1/client-user/**";

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
                        "/api/v1/auth/**",
                        "/api/v1/user/forgot-password",
                        "/api/v1/user/reset-password")
                .permitAll()
                .requestMatchers(HttpMethod.GET,"/api/v1/user/me").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/v1/user/{id}").authenticated()
                .requestMatchers(HttpMethod.PUT,"/api/v1/user/authenticated/{id}").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/v1/user/ifPassMatch").authenticated()
                .requestMatchers(HttpMethod.PUT,"/api/v1/user/change-pass").authenticated()
                .requestMatchers("/api/v1/user/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET,"/api/v1/customerCustomPrice/findByClientAndProduct").hasAnyRole(CUSTOMER.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers("/api/v1/customerCustomPrice/**").hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, CLIENT_URL).hasAnyRole(ADMIN.name(), PRODUCTION_MANAGER.name(), TRANSPORT_MANAGER.name(), CUSTOMER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/client", "/api/v1/upload/file").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, CLIENT_URL).hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, CLIENT_URL).hasAnyRole(ADMIN.name())


                .requestMatchers(HttpMethod.GET, PACKAGE_URL).hasAnyRole(TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name(), ADMIN.name(), CUSTOMER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/package").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, PACKAGE_URL).hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, PACKAGE_URL).hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, PLATE_URL).hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(),TRANSPORT_MANAGER.name(), CUSTOMER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/plate").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, PLATE_URL).hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, PLATE_URL).hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, CARTON_URL).hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(),TRANSPORT_MANAGER.name(), CUSTOMER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/carton").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, CARTON_URL).hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, CARTON_URL).hasAnyRole(ADMIN.name())
          
                .requestMatchers(CLIENT_USER_URL).hasAnyRole(ADMIN.name(), CUSTOMER.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())

//                 .requestMatchers(HttpMethod.GET,"/api/v1/client-user").hasAnyRole(ADMIN.name(),CUSTOMER.name())
//                 .requestMatchers(CLIENT_USER_URL).hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, PRODUCT_URL).hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name(), CUSTOMER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/product").hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/product/produce/***").hasAnyRole(ADMIN.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, PRODUCT_URL).hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, PRODUCT_URL).hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, ORDER_URL).hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/order").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), CUSTOMER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, ORDER_URL).hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, ORDER_URL).hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, ORDER_PRODUCT_URL).hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, ORDER_PRODUCT_URL).hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), CUSTOMER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, ORDER_PRODUCT_URL).hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, ORDER_PRODUCT_URL).hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, MONTHLY_ORDER_URL).hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/monthlyOrder").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, MONTHLY_ORDER_URL).hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, MONTHLY_ORDER_URL).hasAnyRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, MONTHLY_ORDER_PRODUCT_URL).hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/monthlyOrderProduct").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, MONTHLY_ORDER_PRODUCT_URL).hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.DELETE, MONTHLY_ORDER_PRODUCT_URL).hasAnyRole(ADMIN.name())

                .requestMatchers("/api/v1/material-order/**").hasAnyRole(PRODUCTION_MANAGER.name(), ADMIN.name())

                .requestMatchers(HttpMethod.GET, INVOICE_URL).hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/invoice/number/**").hasAnyRole(ADMIN.name(), TRANSPORT_MANAGER.name(), PRODUCTION_MANAGER.name())
                .requestMatchers(HttpMethod.POST, INVOICE_URL).hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, INVOICE_URL).hasAnyRole(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, INVOICE_URL).hasAnyRole(ADMIN.name())
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
