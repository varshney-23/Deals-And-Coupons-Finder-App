package com.casestudy.product_service.service;

import com.casestudy.product_service.models.Product;
import com.casestudy.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository(); // custom dummy repo
        productService = new ProductService(productRepository);
    }

    @Test
    void testAddProduct() {
        Product product = new Product(1L, "Nike", 4.5, "logo.jpg", "Fashion");
        ResponseEntity<?> response = productService.addProduct(product);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetAllProducts() {
        productService.addProduct(new Product(1L, "Nike", 4.5, "logo.jpg", "Fashion"));
        ResponseEntity<?> response = productService.getAllProducts();
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void testGetProductById_NotFound() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(10L);
        });
        assertTrue(exception.getMessage().contains("not found"));
    }
}
