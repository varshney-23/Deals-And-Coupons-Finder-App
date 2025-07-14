package com.casestudy.inventory_service.repository;

import com.casestudy.inventory_service.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByBrandNameIgnoreCase(String brandName);
    List<Brand> findAll();
}
