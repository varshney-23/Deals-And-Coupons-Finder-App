package com.casestudy.coupon_service.service;

import com.casestudy.coupon_service.model.Coupon;
import com.casestudy.coupon_service.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CouponServiceTest {

    private CouponService couponService;
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        couponRepository = new InMemoryCouponRepository(); // custom dummy repo
        couponService = new CouponService(couponRepository);
    }

    @Test
    void testAddCoupon() {
        Coupon coupon = new Coupon(1L, "Nike", "logo.jpg", "Fashion", 10, "50% OFF", "promotional");
        assertNotNull(couponService.addCoupon(coupon));
    }

    @Test
    void testGetPromotionalCoupons() {
        couponService.addCoupon(new Coupon(1L, "Nike", "logo.jpg", "Fashion", 10, "50% OFF", "promotional"));
        List<?> list = couponService.getCouponsByType("promotional");
        assertEquals(1, list.size());
    }

    @Test
    void testDeleteCouponById() {
        Coupon coupon = new Coupon(1L, "Nike", "logo.jpg", "Fashion", 10, "50% OFF", "promotional");
        couponService.addCoupon(coupon);
        couponService.deleteCouponById(1L);
        assertThrows(RuntimeException.class, () -> couponService.getCouponById(1L));
    }
}
