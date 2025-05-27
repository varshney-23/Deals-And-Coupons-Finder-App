package com.dealsandcoupons.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/auth/**")
                        .uri("http://localhost:8081"))
                .route("product-service", r -> r.path("/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config("ADMIN")))) // Only allow ADMIN role
                        .uri("http://localhost:8082"))
                .route("cart-service", r -> r.path("/cart/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config("USER"))))
                        .uri("http://localhost:8083"))
                .route("payment-service", r -> r.path("/payment/**")
                        .uri("http://localhost:8085"))
                .build();
    }
}
