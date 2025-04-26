package com.casestudy.product_service.service;

import com.casestudy.product_service.dto.ResponseDTO;
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
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public ResponseEntity<List<ResponseDTO>> getAllProducts() {
        List<ResponseDTO> products = productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    public ResponseEntity<ResponseDTO> addProduct(Product product) {
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    public ResponseEntity<ResponseDTO> getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new UserException("Product with ID " + id + " not found"));
        return ResponseEntity.ok(convertToDTO(product));
    }

    public ResponseEntity<ResponseDTO> updateProduct(Product product) {
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

    public ResponseEntity<List<ResponseDTO>> findByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        List<ResponseDTO> responseList = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    private ResponseDTO convertToDTO(Product product) {
        return new ResponseDTO(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice()
        );
    }
}
