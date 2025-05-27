package com.dealsandcoupons.user_service.controller;

import com.dealsandcoupons.user_service.dto.LoginDTO;
import com.dealsandcoupons.user_service.dto.RegisterDTO;
import com.dealsandcoupons.user_service.model.Role;
import com.dealsandcoupons.user_service.model.User;
import com.dealsandcoupons.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService authService;

    @InjectMocks
    private UserController userController;

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        registerDTO = new RegisterDTO("testUser", "test@example.com", "password123", Role.USER);
        loginDTO = new LoginDTO("testUser", "password123");

        user = new User();
        user.setId(1L);
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setRole(registerDTO.getRole());
    }

    @Test
    void testRegisterUser() throws Exception {
        when(authService.registerUser(any(RegisterDTO.class))).thenReturn("User registered successfully!");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"email\": \"test@example.com\", \"password\": \"password123\", \"role\": \"USER\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void testLoginUserSuccess() throws Exception {
        when(authService.authenticateUser(any(LoginDTO.class))).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful!"))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void testLoginUserUnauthorized() throws Exception {
        when(authService.authenticateUser(any(LoginDTO.class))).thenReturn("Invalid email or password!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"password\": \"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password!"));
    }

    @Test
    void testGetUserByIdSuccess() throws Exception {
        when(authService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        when(authService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/auth/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserByUsernameSuccess() throws Exception {
        when(authService.getUserByUsername("testUser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/users/username/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testGetUserByUsernameNotFound() throws Exception {
        when(authService.getUserByUsername("unknownUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/auth/users/username/unknownUser"))
                .andExpect(status().isNotFound());
    }
}