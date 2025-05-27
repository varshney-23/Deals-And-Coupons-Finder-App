package com.dealsandcoupons.user_service.controller;

import com.dealsandcoupons.user_service.dto.LoginDTO;
import com.dealsandcoupons.user_service.dto.RegisterDTO;
import com.dealsandcoupons.user_service.dto.UserDTO;
import com.dealsandcoupons.user_service.exception.InvalidCredentialsException;
import com.dealsandcoupons.user_service.exception.InvalidUserException;
import com.dealsandcoupons.user_service.exception.UserNotFoundException;
import com.dealsandcoupons.user_service.model.User;
import com.dealsandcoupons.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService authService;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO requestDTO) {
        if (requestDTO.getUsername() == null || requestDTO.getUsername().trim().isEmpty()) {
            throw new InvalidUserException("Username cannot be empty");
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().trim().isEmpty()) {
            throw new InvalidUserException("Email cannot be empty");
        }
        if (requestDTO.getPassword() == null || requestDTO.getPassword().length() < 6) {
            throw new InvalidUserException("Password must be at least 6 characters long");
        }
        if (requestDTO.getRole() == null || requestDTO.getRole().name().isEmpty()) {
            throw new InvalidUserException("Role cannot be empty");
        }

        String response = authService.registerUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", response));
    }


    // Login User and Generate JWT Token
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            String token = authService.authenticateUser(loginDTO);
            return ResponseEntity.ok(Map.of("message", "Login successful!", "token", token));
        } catch (UserNotFoundException | InvalidCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = authService.getUserById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()));
    }

    @GetMapping("/users/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = authService.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()));
    }



}