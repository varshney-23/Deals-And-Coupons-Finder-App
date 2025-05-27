package com.dealsandcoupons.user_service.controller;

import com.dealsandcoupons.user_service.config.JwtUtil;
import com.dealsandcoupons.user_service.model.User;
import com.dealsandcoupons.user_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    //MockMvc is an entry point and used to perform http request through test classes
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    //http request, request path, return type, input or parameter passing, response status code, res body
    //always write throws exception, because mockmvc methods throw checked exceptions
    @Test
    void registerUser() throws Exception {
        String req = """
                {
                    "username": "test1",
                    "email": "test1@gmail.com",
                    "password": "qwerty123"
                }
                """;

        when(authService.registerUser(Mockito.any())).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void loginUser() throws Exception {
        String login = """
                    {
                        "email": "test1@gmail.com",
                        "password": "qwerty123"
                    }
                """;

        when(authService.authenticateUser(Mockito.any())).thenReturn(ResponseEntity.ok("jwt-token"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful!"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void getUserProfile() throws Exception {
        String token = "valid-token";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test1");

        when(authService.getUserProfile(token)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/auth/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUser.getId()))
                .andExpect(jsonPath("$.username").value(mockUser.getUsername()));

    }
}