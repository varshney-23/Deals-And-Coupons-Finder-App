package com.dealsandcoupons.user_service.service;

import com.dealsandcoupons.user_service.config.JwtUtil;
import com.dealsandcoupons.user_service.dto.LoginRequestDTO;
import com.dealsandcoupons.user_service.dto.RequestDTO;
import com.dealsandcoupons.user_service.model.Role;
import com.dealsandcoupons.user_service.model.User;
import com.dealsandcoupons.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private RequestDTO requestDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        requestDTO = new RequestDTO();
        requestDTO.setUsername("test1");
        requestDTO.setEmail("test1@gmail.com");
        requestDTO.setPassword("qwerty123");

        mockUser = new User();
        mockUser.setUsername("test1");
        mockUser.setEmail("test1@gmail.com");
        mockUser.setPassword(bCryptPasswordEncoder.encode("qwerty123"));
        mockUser.setRole(Role.USER);

    }

    @Test
    void registerUser_success() {
        when(userRepository.findByUsername("test1")).thenReturn(Optional.empty());

        when(userRepository.findByEmail("test1@gmail.com")).thenReturn(Optional.empty());

        User mockUser = new User();

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        //when=>
        ResponseEntity<String> res = authService.registerUser(requestDTO);

        //then=>
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("User registered successfully!", res.getBody());
    }

    @Test
    void registerUser_UsernameExists() {
        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(new User()));

        ResponseEntity<String> res = authService.registerUser(requestDTO);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("Username already exists!", res.getBody());
    }

    @Test
    void registerUser_EmailExists() {
        when(userRepository.findByEmail("test1@gmail.com")).thenReturn(Optional.of(new User()));
        ResponseEntity<String> res = authService.registerUser(requestDTO);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("Email already exists!", res.getBody());
    }

    @Test
    void authenticateUser_Success() {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("test1@gmail.com");
        login.setPassword("qwerty123");

        when(userRepository.findByEmail("test1@gmail.com")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(mockUser.getUsername(), mockUser.getRole().name())).thenReturn("mock-jwt-token");

        ResponseEntity<String> res = authService.authenticateUser(login);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("mock-jwt-token", res.getBody());
    }

    @Test
    void authenticateUser_EmailNotExists(){
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("notfound@gmail.com");
        login.setPassword("blahhh");

        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        ResponseEntity<String> res = authService.authenticateUser(login);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertEquals("User not found with this email!", res.getBody());
    }

    @Test
    void authenticateUser_WrongPassword() {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("test1@gmail.com");
        login.setPassword("galatPassword");

        when(userRepository.findByEmail("test1@gmail.com")).thenReturn(Optional.of(mockUser));

        ResponseEntity<String> res = authService.authenticateUser(login);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("Invalid email or password!", res.getBody());
    }

    //failure success =>
    @Test
    void getUserProfile_success() {
        when(jwtUtil.validateToken("correct-token")).thenReturn("test1");
        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(mockUser));

        Optional<User> opt = authService.getUserProfile("correct-token");
        assertTrue(opt.isPresent());
        assertEquals("test1", opt.get().getUsername());
    }

    @Test
    void getUserProfile_fail() {
        when(jwtUtil.validateToken("wrong-token")).thenReturn("test1");

        Optional<User> opt = authService.getUserProfile("wrong-token");
        assertFalse(opt.isPresent());
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User user = authService.getUserById(1L);
        assertEquals("test1", user.getUsername());
    }

    @Test
    void getUserById_failure_andThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exep = assertThrows(RuntimeException.class, () -> authService.getUserById(1L));
        assertEquals("User not found", exep.getMessage());
    }

    @Test
    void userExists_true() {
        when(userRepository.existsById(1L)).thenReturn(true);
        Boolean exists = authService.userExists(1L);
        assertTrue(exists);
    }

    @Test
    void userExists_Fail() {
        when(userRepository.existsById(1L)).thenReturn(false);
        Boolean exists = authService.userExists(1L);
        assertFalse(exists);
    }
}