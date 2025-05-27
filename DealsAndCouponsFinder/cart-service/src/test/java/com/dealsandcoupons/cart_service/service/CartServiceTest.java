package com.dealsandcoupons.cart_service.service;

import com.dealsandcoupons.cart_service.client.ProductClient;
import com.dealsandcoupons.cart_service.client.UserClient;
import com.dealsandcoupons.cart_service.dto.CartItemDTO;
import com.dealsandcoupons.cart_service.dto.ProductDTO;
import com.dealsandcoupons.cart_service.dto.UserDTO;
import com.dealsandcoupons.cart_service.model.CartItem;
import com.dealsandcoupons.cart_service.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private CartService cartService;

    private CartItemDTO cartItemDTO;
    private CartItem cartItem;
    private UserDTO userDTO;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        cartItemDTO = new CartItemDTO("testUser", 1L, "Test Product", 100.0, 2);
        cartItem = new CartItem(1L, "testUser", 1L,"paid", "Test Product", 100.0, 2);
        userDTO = new UserDTO(1L, "testUser", "Test Name", "test@example.com");
        productDTO = new ProductDTO(1L, "Sample Product", "Type", "Company", "Description",
                "Category", 99.99, 10.0, LocalDate.now(), LocalDate.now().plusDays(30),
                true, "Merchant", "http://image.url");
    }

    @Test
    void testAddItemToCart() {
        when(userClient.getUserByUsername(cartItemDTO.getUsername())).thenReturn(userDTO);
        when(productClient.getProductById(cartItemDTO.getProductId())).thenReturn(productDTO);
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemDTO result = cartService.addItemToCart(cartItemDTO);

        assertNotNull(result);
        assertEquals(cartItemDTO.getUsername(), result.getUsername());
        assertEquals(cartItemDTO.getProductName(), result.getProductName());
        verify(cartRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testGetCartItems() {
        when(userClient.getUserByUsername("testUser")).thenReturn(userDTO);
        when(cartRepository.findByUsername("testUser")).thenReturn(List.of(cartItem));

        List<CartItemDTO> result = cartService.getCartItems("testUser");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(cartItemDTO.getUsername(), result.get(0).getUsername());
    }

    @Test
    void testRemoveItemFromCart() {
        when(userClient.getUserByUsername("testUser")).thenReturn(userDTO);
        when(productClient.getProductById(1L)).thenReturn(productDTO);
        when(cartRepository.findByUsernameAndProductId("testUser", 1L)).thenReturn(Optional.of(cartItem));

        cartService.removeItemFromCart("testUser", 1L);

        verify(cartRepository, times(1)).delete(cartItem);
    }

    @Test
    void testClearCart() {
        when(userClient.getUserByUsername("testUser")).thenReturn(userDTO);
        when(cartRepository.findByUsername("testUser")).thenReturn(List.of(cartItem));

        cartService.clearCart("testUser");

        verify(cartRepository, times(1)).deleteAll(anyList());
    }
}