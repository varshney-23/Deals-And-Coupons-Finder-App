package com.dealsandcoupons.cart_service.client;

import  com.dealsandcoupons.cart_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/auth/users/username/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);


}
