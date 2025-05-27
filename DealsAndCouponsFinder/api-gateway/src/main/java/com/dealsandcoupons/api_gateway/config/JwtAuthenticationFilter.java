package com.dealsandcoupons.api_gateway.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter implements GatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public static class Config {
        private String role;

        public Config() {}
        public Config(String role) { this.role = role; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // Check if the Authorization header is missing or invalid
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("Authorization header is missing or invalid");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            System.out.println("Token: " + token);  // Print the token for debugging

            DecodedJWT jwt = jwtUtil.validateToken(token);
            if (jwt == null) {
                System.out.println("Invalid token");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Extract the role from the JWT
            String role = jwtUtil.extractRole(jwt);
            System.out.println("Extracted role: " + role);// Print the extracted role

            // Role-based access for product service routes
            if (path.startsWith("/products/admin") && !role.equalsIgnoreCase("ADMIN")) {
                System.out.println("Access denied - insufficient role");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Add more role checks as needed for other paths (like product viewing for users)
            if (path.startsWith("/products") && role.equalsIgnoreCase("USER")) {
                // Allow users to access general product routes
                return chain.filter(exchange);
            }

            // ✅ Add check for cart-service routes (for USER only)
            if (path.startsWith("/cart") && !role.equalsIgnoreCase("USER")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            String username = jwt.getSubject();
            System.out.println(jwt.getSubject());
            // Mutate the request to add the X-User-Name header
            exchange = exchange.mutate()
                    .request(builder -> builder.header("X-User-Name", username))
                    .build();

            return chain.filter(exchange);
        };
    }

}
