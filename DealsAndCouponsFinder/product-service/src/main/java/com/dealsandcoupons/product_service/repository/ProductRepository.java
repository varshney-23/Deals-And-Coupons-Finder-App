package com.dealsandcoupons.product_service.repository;

import com.dealsandcoupons.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(String type);
    List<Product> findByCategory(String category);
    List<Product> findByCompanyName(String companyName);
    List<Product> findByIsActive(boolean isActive);
}
