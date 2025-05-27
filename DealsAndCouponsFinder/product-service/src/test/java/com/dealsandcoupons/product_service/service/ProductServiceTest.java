package com.dealsandcoupons.product_service.service;

import com.dealsandcoupons.product_service.model.Product;
import com.dealsandcoupons.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Coupon");
        sampleProduct.setType("FREE");
        sampleProduct.setCompanyName("Flipkart");
    }

    @Test
    void testSaveProduct() {
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);
        Product saved = productService.saveProduct(sampleProduct);
        assertEquals("Test Coupon", saved.getName());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void testGetAllProducts() {
        List<Product> list = Arrays.asList(sampleProduct);
        when(productRepository.findAll()).thenReturn(list);
        List<Product> result = productService.getAllProducts();
        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        Optional<Product> result = productService.getProductById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test Coupon", result.get().getName());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Product> result = productService.getProductById(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProductById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetByType() {
        when(productRepository.findByType("FREE")).thenReturn(List.of(sampleProduct));
        List<Product> result = productService.getByType("FREE");
        assertEquals(1, result.size());
        assertEquals("FREE", result.get(0).getType());
    }

    @Test
    void testGetByCompanyName() {
        when(productRepository.findByCompanyName("Flipkart")).thenReturn(List.of(sampleProduct));
        List<Product> result = productService.getByCompanyName("Flipkart");
        assertEquals(1, result.size());
        assertEquals("Flipkart", result.get(0).getCompanyName());
    }
}