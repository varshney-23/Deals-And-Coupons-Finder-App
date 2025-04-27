package com.casestudy.coupon_service.controller;

import com.casestudy.coupon_service.dto.CouponResponseDTO;
import com.casestudy.coupon_service.model.Coupon;
import com.casestudy.coupon_service.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // for admin ->

    @PostMapping("/admin/add")
    public ResponseEntity<CouponResponseDTO> addCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.addCoupon(coupon));
    }

    @PutMapping("/admin/update")
    public ResponseEntity<CouponResponseDTO> updateCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.updateCoupon(coupon));
    }

    @DeleteMapping("/admin/delete/id/{couponId}")
    public ResponseEntity<String> deleteCouponById(@PathVariable Long couponId) {
        couponService.deleteCouponById(couponId);
        return ResponseEntity.ok("Coupon deleted successfully with ID: " + couponId);
    }

    @DeleteMapping("/admin/delete/brand/{brand}")
    public ResponseEntity<String> deleteCouponByBrand(@PathVariable String brand) {
        couponService.deleteCouponByBrand(brand);
        return ResponseEntity.ok("Coupons deleted successfully for brand: " + brand);
    }

    @GetMapping("/admin/view/all")
    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    // for user ->

    @GetMapping("/user/view/brand/{brand}")
    public ResponseEntity<List<CouponResponseDTO>> getCouponsByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(couponService.getCouponsByBrand(brand));
    }

    @GetMapping("/user/view/category/{category}")
    public ResponseEntity<List<CouponResponseDTO>> getCouponsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(couponService.getCouponsByCategory(category));
    }

    @GetMapping("/user/view/id/{couponId}")
    public ResponseEntity<CouponResponseDTO> getCouponById(@PathVariable Long couponId) {
        return ResponseEntity.ok(couponService.getCouponById(couponId));
    }
}
