package com.casestudy.api_gateway.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String secret;

    public void validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("MyApp")
                .build();
        verifier.verify(token); // throws exception if invalid
    }

    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }

    public String extractRole(String token) {
        return JWT.decode(token).getClaim("role").asString();
    }
}
