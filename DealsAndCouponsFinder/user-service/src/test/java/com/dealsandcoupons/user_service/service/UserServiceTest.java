package com.dealsandcoupons.user_service.service;

import com.dealsandcoupons.user_service.config.JwtUtil;
import com.dealsandcoupons.user_service.dto.LoginDTO;
import com.dealsandcoupons.user_service.dto.RegisterDTO;
import com.dealsandcoupons.user_service.model.Role;
import com.dealsandcoupons.user_service.model.User;
import com.dealsandcoupons.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private User user;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO("testUser",
                "test@example.com",
                "password123",
                Role.USER);
        loginDTO = new LoginDTO("testUser", "password123");

        user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole());
    }

    @Test
    void testRegisterUserSuccess() {
        when(userRepository.findByUsername(registerDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.registerUser(registerDTO);

        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        when(userRepository.findByUsername(registerDTO.getUsername())).thenReturn(Optional.of(user));

        String result = userService.registerUser(registerDTO);

        assertEquals("User already exists", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticateUserSuccess() {
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mocked-jwt-token");

        String result = userService.authenticateUser(loginDTO);

        assertEquals("mocked-jwt-token", result);
    }

    @Test
    void testAuthenticateUserInvalidCredentials() {
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));

        loginDTO.setPassword("wrongPassword");
        String result = userService.authenticateUser(loginDTO);

        assertEquals("Invalid email or password!", result);
    }

    @Test
    void testAuthenticateUserNotFound() {
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty());

        String result = userService.authenticateUser(loginDTO);

        assertEquals("User not found!", result);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }
}