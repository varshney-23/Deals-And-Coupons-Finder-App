package com.dealsandcoupons.product_service.controller;

import com.dealsandcoupons.product_service.model.Product;
import com.dealsandcoupons.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product product;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Test Coupon", "FREE", "Flipkart", "Discount on Flipkart", "Electronics", 100.0, 10.0, LocalDate.now(), LocalDate.now().plusDays(10), true, "Merchant", "http://image.url");
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Coupon"));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Flipkart"));
    }

    @Test
    void testAddProduct() throws Exception {
        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("FREE"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProductById(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetProductsByType() throws Exception {
        when(productService.getByType("FREE")).thenReturn(List.of(product));

        mockMvc.perform(get("/products/type/FREE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("FREE"));
    }

    @Test
    void testGetProductsByCompany() throws Exception {
        when(productService.getByCompanyName("Flipkart")).thenReturn(List.of(product));

        mockMvc.perform(get("/products/company/Flipkart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").value("Flipkart"));
    }
}
