package com.casestudy.api_gateway.filter;


import com.casestudy.api_gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private final List<String> openApiEndpoints = List.of(
            "/auth/register", "/auth/login"
    );

    private final Map<String, List<String>> routeRoleMap = Map.ofEntries(
            // COUPONS
            entry("/inventory/coupon/add", List.of("ROLE_ADMIN")),
            entry("/inventory/coupon/update", List.of("ROLE_ADMIN")),
            entry("/inventory/coupon/delete", List.of("ROLE_ADMIN")),
            entry("/inventory/coupon/delete-expired", List.of("ROLE_ADMIN")),
            entry("/inventory/coupon/get/", List.of("ROLE_ADMIN", "ROLE_USER")),
            entry("/inventory/coupon/all", List.of("ROLE_ADMIN", "ROLE_USER")),
            entry("/inventory/coupon/get/brand", List.of("ROLE_ADMIN", "ROLE_USER")),
            entry("/inventory/coupon/get/category", List.of("ROLE_ADMIN", "ROLE_USER")),

            // BOOKINGS
            entry("/inventory/booking/promotional", List.of("ROLE_USER")),
            entry("/inventory/booking/paid", List.of("ROLE_USER")),
            entry("/inventory/booking/payment", List.of("ROLE_USER")),
            entry("/inventory/booking/user/", List.of("ROLE_USER")),
            entry("/inventory/booking/all", List.of("ROLE_ADMIN")),

            // AUTH
            entry("/auth/profile", List.of("ROLE_USER", "ROLE_ADMIN"))
    );



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (isOpenApi(path)) {
            return chain.filter(exchange);
        }

        List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty("Authorization");
        if (authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeaders.get(0).substring(7); // remove Bearer

        try {
            jwtUtil.validateToken(token);
            String role = jwtUtil.extractRole(token);
            log.info("Token validated. Role: {}", role);

            if (!isAuthorized(path, role)) {
                log.warn("Forbidden: role {} not allowed to access {}", role, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isOpenApi(String path) {
        return openApiEndpoints.stream().anyMatch(path::contains);
    }

    private boolean isAuthorized(String path, String role) {
        return routeRoleMap.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getKey().length(), e1.getKey().length()))
                .filter(e->path.startsWith(e.getKey()))
                .findFirst()
                .map(e -> e.getValue().contains(role))
                .orElse(false);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}