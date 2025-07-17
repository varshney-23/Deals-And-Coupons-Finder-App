package com.dealsandcoupons.user_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // 🔓 Allow /auth/login
                        .anyRequest().authenticated() // 🔐 All other routes secured
                )
                .httpBasic(Customizer.withDefaults()); // Optional: enables basic auth (remove if not needed)

        return http.build();
    }
}
