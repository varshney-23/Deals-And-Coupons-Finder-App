package com.dealsandcoupons.cart_service.repository;

import com.dealsandcoupons.cart_service.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUsername(String username); // Changed from userId to username
    Optional<CartItem> findByUsernameAndProductId(String username, Long productId); // Changed from userId to username
    List<CartItem> findByUsernameAndStatus(String username, String status);
}
