package com.casestudy.product_service.Interface;

import com.casestudy.product_service.dto.ProductResponseDTO;
import com.casestudy.product_service.models.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IProductService {

    ResponseEntity<List<ProductResponseDTO>> getAllProducts();
    ResponseEntity<ProductResponseDTO> addProduct(Product product);
    ResponseEntity<ProductResponseDTO> getProductById(Long id);
    ResponseEntity<ProductResponseDTO> updateProduct(Product product);
    ResponseEntity<String> deleteProductById(Long id);
    ResponseEntity<List<ProductResponseDTO>> findByCategory(String category);

}
