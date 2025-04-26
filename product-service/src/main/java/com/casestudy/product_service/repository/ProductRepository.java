package com.casestudy.product_service.repository;

import com.casestudy.product_service.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Optional<Product>> findByCategory(String category);
}
