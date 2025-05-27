package com.dealsandcoupons.api_gateway.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public DecodedJWT validateToken(String token) {
        try {
            System.out.println("Using Secret: " + secret);  // Ensure it matches during token creation
            Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());  // Ensure secret is passed as bytes
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("MyApp")  // Ensure issuer matches
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return null;  // Invalid token
        }
    }

    public String extractUsername(DecodedJWT jwt) {
        return jwt.getSubject();
    }

    public String extractRole(DecodedJWT jwt) {
        try {
            // Assuming your JWT contains a claim named 'role' (a string)
            String roleString = jwt.getClaim("role").asString();
            return roleString;  // Convert the string to Role enum
        } catch (Exception e) {
            return null;  // Return null if there was an issue extracting or converting the role
        }
    }
}
