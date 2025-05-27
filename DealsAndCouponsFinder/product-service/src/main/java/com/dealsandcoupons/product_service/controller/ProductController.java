package com.dealsandcoupons.product_service.controller;

import com.dealsandcoupons.product_service.dto.ProductDTO;
import com.dealsandcoupons.product_service.exception.InvalidProductException;
import com.dealsandcoupons.product_service.exception.ProductNotFoundException;
import com.dealsandcoupons.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/admin/add")
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        // Validate required fields
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new InvalidProductException("Product name cannot be empty");
        }
        if (productDTO.getPrice() <= 0) {
            throw new InvalidProductException("Price must be greater than zero");
        }

        ProductDTO savedProduct = productService.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAll() {
        List<ProductDTO> products = productService.getAllProducts();
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No data Found");
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        return ResponseEntity.ok(product);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO updated) {
        productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        updated.setId(id);
        return ResponseEntity.ok(productService.saveProduct(updated));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ProductDTO>> getByType(@PathVariable String type) {
        List<ProductDTO> products = productService.getByType(type);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found with type: " + type);
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable String category) {
        List<ProductDTO> products = productService.getByCategory(category);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found in category: " + category);
        }
        return ResponseEntity.ok(products);
    }


    @GetMapping("/company/{company}")
    public ResponseEntity<List<ProductDTO>> getByCompany(@PathVariable String company) {
        List<ProductDTO> products = productService.getByCompanyName(company);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found with company name: " + company);
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/active/{status}")
    public ResponseEntity<List<ProductDTO>> getByActiveStatus(@PathVariable boolean status) {
        List<ProductDTO> products = productService.getByActiveStatus(status);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found");
        }
        return ResponseEntity.ok(products);
    }
}
