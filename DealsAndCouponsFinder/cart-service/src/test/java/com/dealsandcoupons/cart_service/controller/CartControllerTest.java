package com.dealsandcoupons.cart_service.controller;

import com.dealsandcoupons.cart_service.dto.CartItemDTO;
import com.dealsandcoupons.cart_service.dto.CartSummaryResponse;
import com.dealsandcoupons.cart_service.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    void testAddItemToCart() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        CartItemDTO cartItemDTO = new CartItemDTO("testUser", 1L, "Product", 100.0, 2);
        when(cartService.addItemToCart(any(CartItemDTO.class))).thenReturn(cartItemDTO);

        mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Name", "testUser")
                        .content("{\"username\": \"testUser\", \"productId\": 1, \"productName\": \"Product\", \"price\": 100.0, \"quantity\": 2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.productName").value("Product"));
    }

    @Test
    void testGetCartItems() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        List<CartItemDTO> cartItems = List.of(new CartItemDTO("testUser", 1L, "Product", 100.0, 2));
        when(cartService.getCartItems("testUser")).thenReturn(cartItems);

        mockMvc.perform(get("/cart/testUser")
                        .header("X-User-Name", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    void testRemoveItemFromCart() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        mockMvc.perform(delete("/cart/testUser/item/1")
                        .header("X-User-Name", "testUser"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).removeItemFromCart("testUser", 1L);
    }

    @Test
    void testClearCart() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        mockMvc.perform(delete("/cart/testUser/clear")
                        .header("X-User-Name", "testUser"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).clearCart("testUser");
    }

    @Test
    void testGetCartSummary() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        CartSummaryResponse summaryResponse = new CartSummaryResponse(List.of("Product 1", "Product 2"), 200.0);
        when(cartService.getCartSummary("testUser")).thenReturn(summaryResponse);

        mockMvc.perform(get("/cart/testUser/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitles.length()").value(2))
                .andExpect(jsonPath("$.totalAmount").value(200.0));
    }
}