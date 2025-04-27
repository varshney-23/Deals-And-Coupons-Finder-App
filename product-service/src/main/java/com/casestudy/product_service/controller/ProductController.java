package com.casestudy.product_service.controller;

import com.casestudy.product_service.dto.ProductRequestDTO;
import com.casestudy.product_service.dto.ProductResponseDTO;
import com.casestudy.product_service.models.Product;
import com.casestudy.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    //user apis->
    @GetMapping("/")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/find/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }

    @PostMapping("/add")
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/update")
    public ResponseEntity<ProductResponseDTO> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }

    //Hidden API: Feign call from Coupon-Service, so that it is not visible at swagger
    @Hidden
    @PostMapping("/admin/add")
    public ResponseEntity<String> addBrandFromCoupon(@RequestBody ProductRequestDTO dto) {
        return productService.addBrandFromCoupon(dto);
    }
}
