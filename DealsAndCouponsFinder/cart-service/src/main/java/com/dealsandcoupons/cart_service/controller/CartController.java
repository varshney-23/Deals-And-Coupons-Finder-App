package com.dealsandcoupons.cart_service.controller;

import com.dealsandcoupons.cart_service.dto.CartItemDTO;
import com.dealsandcoupons.cart_service.dto.CartSummaryResponse;
import com.dealsandcoupons.cart_service.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Add item to cart
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDTO addItemToCart(@RequestBody CartItemDTO cartItemDTO,
                                     @RequestHeader("X-User-Name") String authenticatedUsername) {
        if (!authenticatedUsername.equals(cartItemDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return cartService.addItemToCart(cartItemDTO);
    }

    // View cart items by username
    @GetMapping("/{username}")
    public List<CartItemDTO> getCartItems(@PathVariable String username,
                                          @RequestHeader("X-User-Name") String authenticatedUsername) {
        if (!username.equals(authenticatedUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return cartService.getCartItems(username);
    }

    // Remove item from cart
    @DeleteMapping("/{username}/item/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemFromCart(@PathVariable String username,
                                   @PathVariable Long productId,
                                   @RequestHeader("X-User-Name") String authenticatedUsername) {
        if (!username.equals(authenticatedUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        cartService.removeItemFromCart(username, productId);
    }

    // Clear user's cart
    @DeleteMapping("/{username}/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable String username,
                          @RequestHeader("X-User-Name") String authenticatedUsername) {
        if (!username.equals(authenticatedUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        cartService.clearCart(username);
    }
    @GetMapping("/{username}/summary")
    public ResponseEntity<CartSummaryResponse> getCartSummary(@PathVariable String username) {
        CartSummaryResponse response = cartService.getCartSummary(username);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update-status/{username}")
    public ResponseEntity<String> updateCartStatus(@PathVariable String username) {
        return ResponseEntity.ok(cartService.updateCartStatus(username));
    }

}
