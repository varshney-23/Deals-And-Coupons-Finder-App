package com.casestudy.inventory_service.feignClient;

import com.casestudy.inventory_service.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("PROFILE-SERVICE")
public interface ProfileClient {

    @GetMapping("/auth/profile")
    ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String token);
}