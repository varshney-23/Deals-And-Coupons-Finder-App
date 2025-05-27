package com.dealsandcoupons.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private String username; // Changed from Long userId to String username
    private Long productId;
    private String productName;
    private Double price;
    private int quantity;
    private String status;
}
