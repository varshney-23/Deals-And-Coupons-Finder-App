package com.casestudy.inventory_service.Interface;

import com.casestudy.inventory_service.dto.CouponResponseDTO;
import com.casestudy.inventory_service.model.Brand;
import com.casestudy.inventory_service.model.Coupons;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICouponService {

    void deleteExpiredCoupons();
    ResponseEntity<CouponResponseDTO> addCoupon(Coupons coupon);
    ResponseEntity<List<CouponResponseDTO>> getAllCoupons();
    ResponseEntity<CouponResponseDTO> getCouponById(Long id);
    ResponseEntity<CouponResponseDTO> updateCoupon(Coupons coupon);
    ResponseEntity<String> deleteCouponById(Long id);
    ResponseEntity<List<CouponResponseDTO>> getCouponsByBrand(String brand);
    ResponseEntity<List<CouponResponseDTO>> getCouponsByCategory(String category);
}
