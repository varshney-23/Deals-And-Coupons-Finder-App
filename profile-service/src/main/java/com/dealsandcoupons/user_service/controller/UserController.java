package com.dealsandcoupons.user_service.controller;

import com.dealsandcoupons.user_service.config.JwtUtil;
import com.dealsandcoupons.user_service.dto.LoginRequestDTO;
import com.dealsandcoupons.user_service.dto.RequestDTO;
import com.dealsandcoupons.user_service.model.User;
import com.dealsandcoupons.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RequestDTO user) {
        return authService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO requestDTO) {
        ResponseEntity<String> tokenResponse = authService.authenticateUser(requestDTO);

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", tokenResponse.getBody()));
        }

        return ResponseEntity.ok(Map.of("message", "Login successful!", "token", tokenResponse.getBody()));
    }

    //feign client route =>
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Optional<User> user = authService.getUserProfile(token);

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

}