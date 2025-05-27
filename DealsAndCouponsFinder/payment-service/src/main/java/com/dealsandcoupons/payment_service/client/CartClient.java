package com.dealsandcoupons.payment_service.client;

import com.dealsandcoupons.payment_service.dto.CartSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "cart-service")  // this name must match the spring.application.name in cart-service
public interface CartClient {
    @GetMapping("/cart/{username}/summary")
    CartSummaryResponse getCartDetailsByUsername(@PathVariable String username);
    @PutMapping("/cart/update-status/{username}")
    String updateCartStatus(@PathVariable String username);
}
