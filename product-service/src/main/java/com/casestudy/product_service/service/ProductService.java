package com.casestudy.product_service.service;

import com.casestudy.product_service.Interface.IProductService;
import com.casestudy.product_service.dto.ProductRequestDTO;
import com.casestudy.product_service.dto.ProductResponseDTO;
import com.casestudy.product_service.exception.UserException;
import com.casestudy.product_service.models.Product;
import com.casestudy.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    @Autowired
    ProductRepository productRepository;

    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    public ResponseEntity<ProductResponseDTO> addProduct(Product product) {
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    public ResponseEntity<ProductResponseDTO> getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new UserException("Product with ID " + id + " not found"));
        return ResponseEntity.ok(convertToDTO(product));
    }

    public ResponseEntity<ProductResponseDTO> updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new UserException("Cannot update, product not found with ID: " + product.getId());
        }
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    public ResponseEntity<String> deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new UserException("Cannot delete, product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }

    public ResponseEntity<List<ProductResponseDTO>> findByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        List<ProductResponseDTO> responseList = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    //adding brand from coupon
    public ResponseEntity<String> addBrandFromCoupon(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setImageUrl(dto.getLogoUrl());
        product.setCategory(dto.getCategory());
        product.setRating(5.0); // Default rating
        productRepository.save(product);
        return ResponseEntity.ok("Brand added successfully in Product-Service!");
    }

    private ProductResponseDTO convertToDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getCategory()
        );
    }
}
