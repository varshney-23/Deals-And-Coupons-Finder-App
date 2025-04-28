package com.casestudy.coupon_service.repository;


import com.casestudy.coupon_service.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByBrand(String brand);
    List<Coupon> findByCategory(String category);
    List<Coupon> findByCoupType(String coupType);
}
