package com.casestudy.inventory_service.controller;

import com.casestudy.inventory_service.Interface.IBookingService;
import com.casestudy.inventory_service.Interface.ICouponService;
import com.casestudy.inventory_service.dto.*;
import com.casestudy.inventory_service.model.Coupons;
import com.casestudy.inventory_service.repository.BookingRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    // -------------------- COUPON APIs --------------------

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/coupon/add/promotional")
    public ResponseEntity<CouponResponseDTO> addPromoCoupon(@Valid @RequestBody PromoCouponDTO coupon) {
        return couponService.addPromoCoupon(coupon);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/coupon/add/paid")
    public ResponseEntity<CouponResponseDTO> addPaidCoupon(@Valid @RequestBody PaidCouponDTO coupon) {
        return couponService.addPaidCoupon(coupon);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/coupon/update")
    public ResponseEntity<CouponResponseDTO> updateCoupon(@Valid @RequestBody CouponUpdateDTO dto) {
        return couponService.updateCoupon(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/coupon/delete/{id}")
    public ResponseEntity<String> deleteCouponById(@PathVariable Long id) {
        return couponService.deleteCouponById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/coupon/delete-expired")
    public void deleteExpiredCoupons() {
        couponService.deleteExpiredCoupons();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/coupon/get/{id}")
    public ResponseEntity<CouponResponseDTO> getCouponById(@PathVariable Long id) {
        return couponService.getCouponById(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/coupon/all")
    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/coupon/get/brand/{brand}")
    public ResponseEntity<List<CouponResponseDTO>> getCouponsByBrand(@Valid @PathVariable String brand) {
        return couponService.getCouponsByBrand(brand);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/coupon/get/category/{category}")
    public ResponseEntity<List<CouponResponseDTO>> getCouponsByCategory(@Valid @PathVariable String category) {
        return couponService.getCouponsByCategory(category);
    }

    // ------------------- BOOKING APIs --------------------

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/booking/promotional/{couponId}")
    public ResponseEntity<BookingResponseDTO> bookPromotional(
            @PathVariable Long couponId ,
            @RequestHeader("Authorization") String token) {
        return bookingService.bookPromotional(couponId, token);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/booking/paid")
    public ResponseEntity<BookingResponseDTO> bookPaid(@Valid @RequestBody BookingRequestDTO requestDTO , @RequestHeader("Authorization") String token) {
        return bookingService.bookPaidCoupon(requestDTO, token);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/booking/payment/{bookingId}")
    public ResponseEntity<String> completePayment(@PathVariable Long bookingId) {
//        System.out.println("hit ho bhi raha hai?");
        return bookingService.completePayment(bookingId);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/booking/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getUserBookings(@PathVariable Long userId) {
        return bookingService.getUserBookings(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/booking/all")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return bookingService.getAllBookings();
    }

}