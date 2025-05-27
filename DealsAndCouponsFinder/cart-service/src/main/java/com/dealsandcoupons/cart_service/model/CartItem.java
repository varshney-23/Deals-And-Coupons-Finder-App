package com.dealsandcoupons.cart_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // Changed from Long userId to String username

    @Column(nullable = false)
    private Long productId;
    private String status;
    private String productName;
    private Double price;
    private int quantity;
}
