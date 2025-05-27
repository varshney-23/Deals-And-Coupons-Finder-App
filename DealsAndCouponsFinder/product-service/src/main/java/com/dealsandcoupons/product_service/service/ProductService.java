package com.dealsandcoupons.product_service.service;

import com.dealsandcoupons.product_service.dto.ProductDTO;
import com.dealsandcoupons.product_service.model.Product;
import com.dealsandcoupons.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public ProductDTO saveProduct(ProductDTO dto) {
        Product product = mapToEntity(dto);
        return mapToDTO(productRepository.save(product));
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id).map(this::mapToDTO);
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    public List<ProductDTO> getByType(String type) {
        return productRepository.findByType(type).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getByCategory(String category) {
        return productRepository.findByCategory(category).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getByCompanyName(String companyName) {
        return productRepository.findByCompanyName(companyName).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getByActiveStatus(boolean isActive) {
        return productRepository.findByIsActive(isActive).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private Product mapToEntity(ProductDTO dto) {
        return new Product(
                dto.getId(),
                dto.getName(),
                dto.getType(),
                dto.getCompanyName(),
                dto.getDescription(),
                dto.getCategory(),
                dto.getPrice(),
                dto.getDiscount(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.isActive(),
                dto.getMerchantName(),
                dto.getImageURL()
        );
    }

    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getType(),
                product.getCompanyName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getDiscount(),
                product.getStartDate(),
                product.getEndDate(),
                product.isActive(),
                product.getMerchantName(),
                product.getImageURL()
        );
    }
}
