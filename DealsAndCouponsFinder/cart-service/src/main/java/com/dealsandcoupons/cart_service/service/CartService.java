package com.dealsandcoupons.cart_service.service;

import com.dealsandcoupons.cart_service.client.ProductClient;
import com.dealsandcoupons.cart_service.client.UserClient;
import com.dealsandcoupons.cart_service.dto.CartItemDTO;
import com.dealsandcoupons.cart_service.dto.CartSummaryResponse;
import com.dealsandcoupons.cart_service.dto.ProductDTO;
import com.dealsandcoupons.cart_service.dto.UserDTO;
import com.dealsandcoupons.cart_service.model.CartItem;
import com.dealsandcoupons.cart_service.repository.CartRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private UserClient userClient;

    // Add item to cart using product-service info
    @CircuitBreaker(name="addToCartCircuit", fallbackMethod = "fallbackAddItemToCart")
    public CartItemDTO addItemToCart(CartItemDTO cartItemDTO) {
        // Fetch user info from user-service to validate user
        UserDTO user = userClient.getUserByUsername(cartItemDTO.getUsername());

        if (user == null) {
            throw new RuntimeException("User not found with username: " + cartItemDTO.getUsername());
        }

        // Fetch product details from product-service
        ProductDTO product = productClient.getProductById(cartItemDTO.getProductId());

        CartItem cartItem = new CartItem();
        cartItem.setUsername(cartItemDTO.getUsername());

        cartItem.setProductId(cartItemDTO.getProductId());
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setProductName(product.getName());
        cartItem.setPrice(product.getPrice());
        cartItem.setStatus("Pending");

        CartItem savedCartItem = cartRepository.save(cartItem);

        return new CartItemDTO(
                savedCartItem.getUsername(),
                savedCartItem.getProductId(),
                savedCartItem.getProductName(),
                savedCartItem.getPrice(),
                savedCartItem.getQuantity(),
                savedCartItem.getStatus()

        );
    }
    public CartItemDTO fallbackAddItemToCart(CartItemDTO cartItemDTO, Throwable throwable) {
        // Log the error or throwable for debugging purposes
        System.err.println("Fallback method triggered due to: " + throwable.getMessage());

        // Return a default CartItemDTO or handle the fallback logic
        return new CartItemDTO(
                cartItemDTO.getUsername(),
                cartItemDTO.getProductId(),
                "Default Product Name",
                0.0,
                cartItemDTO.getQuantity(),
                "pending"
        );
    }
    // View cart items by username
    public List<CartItemDTO> getCartItems(String username) {
        UserDTO user = userClient.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        return cartRepository.findByUsername(username).stream()
                .map(item -> new CartItemDTO(
                        item.getUsername(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getStatus()))
                .collect(Collectors.toList());
    }

    // Remove item from cart
    public void removeItemFromCart(String username, Long productId) {
        UserDTO user = userClient.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        ProductDTO product = productClient.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        CartItem cartItem = cartRepository.findByUsernameAndProductId(username, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found for productId: " + productId));

        cartRepository.delete(cartItem);
    }

    // Clear the user's cart
    public void clearCart(String username) {
        UserDTO user = userClient.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        List<CartItem> cartItems = cartRepository.findByUsername(username);
        cartRepository.deleteAll(cartItems);
    }
    public CartSummaryResponse getCartSummary(String username) {
        List<CartItem> carts = cartRepository.findByUsernameAndStatus(username, "PENDING");
        for(CartItem c: carts){
            System.out.println(c.toString());
        }
        List<String> titles = carts.stream()
                .map(CartItem::getProductName)
                .toList();

        double totalAmount = carts.stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
        System.out.println(totalAmount);
        return new CartSummaryResponse(titles, totalAmount);
    }
    public String updateCartStatus(String username) {
        List<CartItem> cartItems = cartRepository.findByUsernameAndStatus(username, "PENDING");

        if (cartItems.isEmpty()) {
            return "No pending items found for user: " + username;
        }

        cartItems.forEach(cartItem -> cartItem.setStatus("PAID"));
        cartRepository.saveAll(cartItems);

        return "Cart items updated to PAID for user: " + username;
    }

}
