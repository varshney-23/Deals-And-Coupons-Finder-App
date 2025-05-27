package com.dealsandcoupons.user_service.service;
import com.dealsandcoupons.user_service.config.JwtUtil;
import com.dealsandcoupons.user_service.dto.LoginDTO;
import com.dealsandcoupons.user_service.dto.RegisterDTO;
import com.dealsandcoupons.user_service.model.User;
import com.dealsandcoupons.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // Register User
    public String registerUser(RegisterDTO registerDTO) {
        Optional<User> existing = userRepository.findByUsername(registerDTO.getUsername());
        if (existing.isPresent()) {
            return "User already exists";
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole());  // or whatever default role you use

        userRepository.save(user);
        return "User registered successfully!";
    }


    // Authenticate User and Generate Token
    public String authenticateUser(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsername());

        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return "Invalid email or password!";
        }
        System.out.println("Extracted role: " + user.getRole().name());

        return jwtUtil.generateToken(loginDTO.getUsername(), user.getRole().name());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), new ArrayList<>()
        );
    }
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username); // assuming such a method exists
    }

}
