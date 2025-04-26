package com.casestudy.product_service.controller;

import com.casestudy.product_service.dto.ResponseDTO;
import com.casestudy.product_service.models.Product;
import com.casestudy.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/")
    public ResponseEntity<List<ResponseDTO>> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/find/category/{category}")
    public ResponseEntity<List<ResponseDTO>> searchProductsByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseDTO> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }
}
