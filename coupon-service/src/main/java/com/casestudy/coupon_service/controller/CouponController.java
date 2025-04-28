package com.casestudy.coupon_service.controller;

import com.casestudy.coupon_service.dto.CouponResponseDTO;
import com.casestudy.coupon_service.model.Coupon;
import com.casestudy.coupon_service.service.CouponService;
import com.casestudy.coupon_service.utils.CouponsCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    //admin apis->
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

    //user apis->
    @GetMapping("/user/view/brand/{brand}")
    public ResponseEntity<List<CouponResponseDTO>> getCouponsByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(couponService.getCouponsByBrand(brand));
    }

    @GetMapping("/user/get/promotional")
    public ResponseEntity<List<CouponResponseDTO>> getPromotionalCoupons() {
        return ResponseEntity.ok(couponService.getCouponsByType("promotional"));
    }

    @GetMapping("/user/get/purchased")
    public ResponseEntity<List<CouponResponseDTO>> getPurchasedCoupons() {
        return ResponseEntity.ok(couponService.getCouponsByType("purchased"));
    }

    @GetMapping("/user/get/promo/{couponId}")
    public ResponseEntity<String> generatePromoCouponCode(@PathVariable Long couponId) {
        CouponResponseDTO coupon = couponService.getCouponById(couponId);

        if (!coupon.getCoupType().equalsIgnoreCase("promotional")) {
            return ResponseEntity.badRequest().body("Not a promotional coupon!");
        }

        String generatedCode = CouponsCodeGenerator.generateCouponCode(12);
        return ResponseEntity.ok("Your Coupon Code: " + generatedCode);
    }
}
