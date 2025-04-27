package com.casestudy.coupon_service.Interface;

import com.casestudy.coupon_service.dto.CouponResponseDTO;
import com.casestudy.coupon_service.model.Coupon;

import java.util.List;

public interface ICouponService {

    CouponResponseDTO addCoupon(Coupon coupon);
    CouponResponseDTO updateCoupon(Coupon coupon);
    void deleteCouponById(Long couponId);
    void deleteCouponByBrand(String brand);
    List<CouponResponseDTO> getAllCoupons();
    List<CouponResponseDTO> getCouponsByBrand(String brand);
    List<CouponResponseDTO> getCouponsByCategory(String category);
    CouponResponseDTO getCouponById(Long couponId);
}
