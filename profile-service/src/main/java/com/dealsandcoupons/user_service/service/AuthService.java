package com.dealsandcoupons.user_service.service;

import com.dealsandcoupons.user_service.config.JwtUtil;
import com.dealsandcoupons.user_service.dto.LoginRequestDTO;
import com.dealsandcoupons.user_service.dto.RequestDTO;
import com.dealsandcoupons.user_service.model.Role;
import com.dealsandcoupons.user_service.model.User;
import com.dealsandcoupons.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //Register User
    public ResponseEntity<String> registerUser(RequestDTO requestDTO) {
        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }

        requestDTO.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        userRepository.save(convertToEntity(requestDTO));
        return ResponseEntity.ok("User registered successfully!");
    }

    //Login User
    public ResponseEntity<String> authenticateUser(LoginRequestDTO requestDTO) {
        Optional<User> userOpt = userRepository.findByEmail(requestDTO.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found with this email!");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password!");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(token);
    }

    //feign-client's method =>
    public Optional<User> getUserProfile(String token) {
        String username = jwtUtil.validateToken(token);
        if (username == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }

    private User convertToEntity(RequestDTO requestDTO) {
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(requestDTO.getPassword());

        if(requestDTO.getRole() != null){
            user.setRole(requestDTO.getRole());
        } else {
            user.setRole(Role.USER);
        }
        return user;
    }
}