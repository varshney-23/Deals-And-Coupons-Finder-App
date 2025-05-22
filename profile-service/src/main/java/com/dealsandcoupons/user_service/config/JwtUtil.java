package com.dealsandcoupons.user_service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;



@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 60 * 60 * 1000; // 1 hour

    public String generateToken(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", "ROLE_"+role)
                .withIssuer("MyApp")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);//symmetric encryption
            JWTVerifier verifier = JWT.require(algorithm) //helps to verify jwt
                    .withIssuer("MyApp")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token); //inbuilt method
            return decodedJWT.getSubject(); // returns email/username
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("MyApp")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("role").asString(); //claim karna hota authority ko batata h
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}