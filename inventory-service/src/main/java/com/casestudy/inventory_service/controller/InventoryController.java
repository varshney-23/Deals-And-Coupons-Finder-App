package com.casestudy.inventory_service.controller;

import com.casestudy.inventory_service.Interface.IBookingService;
import com.casestudy.inventory_service.Interface.IBrandService;
import com.casestudy.inventory_service.Interface.ICouponService;
import com.casestudy.inventory_service.dto.*;
import com.casestudy.inventory_service.model.Booking;
import com.casestudy.inventory_service.model.Brand;
import com.casestudy.inventory_service.model.Coupons;
import com.casestudy.inventory_service.repository.BookingRepository;
import feign.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private IBrandService brandService;

    @GetMapping("/brands")
    public ResponseEntity<List<BrandResponseDTO>> getAllBrands() {
//        List<Brand> brands = brandService.getAllBrands();
        return brandService.getAllBrands();
    }

    // -------------------- COUPON APIs --------------------

    @PostMapping("/coupon/add/promotional")
    public ResponseEntity<CouponResponseDTO> addPromoCoupon(@Valid @RequestBody PromoCouponDTO coupon) {
        return couponService.addPromoCoupon(coupon);
    }

    @PostMapping("/coupon/add/paid")
    public ResponseEntity<CouponResponseDTO> addPaidCoupon(@Valid @RequestBody PaidCouponDTO coupon) {
        return couponService.addPaidCoupon(coupon);
    }

    @PutMapping("/coupon/update")
    public ResponseEntity<CouponResponseDTO> updateCoupon(@Valid @RequestBody CouponUpdateDTO dto) {
        return couponService.updateCoupon(dto);
    }

    @DeleteMapping("/coupon/delete/{id}")
    public ResponseEntity<String> deleteCouponById(@PathVariable Long id) {
        return couponService.deleteCouponById(id);
    }

    @DeleteMapping("/coupon/delete-expired")
    public void deleteExpiredCoupons() {
        couponService.deleteExpiredCoupons();
    }

    @GetMapping("/coupon/get/{id}")
    public ResponseEntity<CouponResponseDTO> getCouponById(@PathVariable Long id) {
        return couponService.getCouponById(id);
    }

    @GetMapping("/coupon/all")
    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/coupon/get/brand/{brand}")
    public ResponseEntity<List<CouponResponseDTO>> getCouponsByBrand(@PathVariable String brand) {
        return couponService.getCouponsByBrand(brand);
    }

    @GetMapping("/coupon/get/category/{category}")
    public ResponseEntity<List<CouponResponseDTO>> getCouponsByCategory( @PathVariable String category) {
        return couponService.getCouponsByCategory(category);
    }

    // ------------------- BOOKING APIs --------------------

    @PostMapping("/booking/promotional/{couponId}")
    public ResponseEntity<BookingResponseDTO> bookPromotional(
            @PathVariable Long couponId ,
            @RequestHeader("Authorization") String token) {
        return bookingService.bookPromotional(couponId, token);
    }

    @PostMapping("/booking/paid")
    public ResponseEntity<BookingResponseDTO> bookPaid(@Valid @RequestBody BookingRequestDTO requestDTO , @RequestHeader("Authorization") String token) {
        return bookingService.bookPaidCoupon(requestDTO, token);
    }

    @GetMapping("/booking/payment/{bookingId}")
    public ResponseEntity<String> completePayment(@PathVariable Long bookingId) {
//        System.out.println("hit ho bhi raha hai?");
        return bookingService.completePayment(bookingId);
    }

    @GetMapping("/booking/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getUserBookings(@PathVariable Long userId) {
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/booking/all")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return bookingService.getAllBookings();
    }

}