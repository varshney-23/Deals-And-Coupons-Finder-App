package com.casestudy.inventory_service.service;

import com.casestudy.inventory_service.Interface.ICouponService;
import com.casestudy.inventory_service.dto.CouponResponseDTO;
import com.casestudy.inventory_service.Exception.UserException;
import com.casestudy.inventory_service.model.Brand;
import com.casestudy.inventory_service.model.Coupons;
import com.casestudy.inventory_service.repository.BrandRepository;
import com.casestudy.inventory_service.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponService implements ICouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private BrandRepository brandRepository;

    public void deleteExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupons> expired = couponRepository.findByExpiryTimeBefore(now);
        if(!expired.isEmpty()) {
            couponRepository.deleteAll(expired);
            System.out.println("Expired coupons deleted: "+expired.size());
        }
    }

    public ResponseEntity<CouponResponseDTO> addCoupon(Coupons coupon) {
        Brand brand = coupon.getBrand();
        // Null check for brand
        if (brand == null || brand.getBrandName() == null || brand.getBrandName().isEmpty()) {
            throw new IllegalArgumentException("Brand name is required");
        }

        // Check if brand already exists
        Optional<Brand> existingBrand = brandRepository.findByBrandNameIgnoreCase(brand.getBrandName());

        Brand brandToUse = existingBrand.orElseGet(() -> brandRepository.save(brand));
        coupon.setBrand(brandToUse);

        Optional<Coupons> existingCoupon = couponRepository.findByBrandAndOfferDetailsIgnoreCase(
                coupon.getBrand(), coupon.getOfferDetails()
        );

        if (existingCoupon.isPresent()) {
            throw new IllegalArgumentException("Coupon with the same brand and offer already exists");
        }
        Coupons saved = couponRepository.save(coupon);
        return ResponseEntity.ok(convertToDTO(saved));

    }

    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        List<CouponResponseDTO> list = couponRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    public ResponseEntity<CouponResponseDTO> getCouponById(Long id) {
        Coupons coupon = couponRepository.findById(id)
                .orElseThrow(() -> new UserException("Coupon not found with ID: " + id));
        return ResponseEntity.ok(convertToDTO(coupon));
    }

    public ResponseEntity<CouponResponseDTO> updateCoupon(Coupons coupon) {
        if (!couponRepository.existsById(coupon.getCoupon_id())) {
            throw new UserException("Cannot update. Coupon not found with ID: " + coupon.getCoupon_id());
        }
        Coupons updated = couponRepository.save(coupon);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    public ResponseEntity<String> deleteCouponById(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new UserException("Cannot delete. Coupon not found with ID: " + id);
        }
        couponRepository.deleteById(id);
        return ResponseEntity.ok("Coupon deleted successfully!");
    }

    public ResponseEntity<List<CouponResponseDTO>> getCouponsByBrand(String brandName) {
       Brand brand = brandRepository.findByBrandNameIgnoreCase(brandName)
                .orElseThrow(() -> new UserException("Brand not found: " + brandName));

        List<CouponResponseDTO> list = couponRepository.findByBrand(brand);

        if (list.isEmpty()) {
            throw new UserException("No coupons found for brand: " + brand);
        }
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<List<CouponResponseDTO>> getCouponsByCategory(String category) {
        List<CouponResponseDTO> list = couponRepository.findByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new UserException("No coupons found in category: " + category);
        }
        return ResponseEntity.ok(list);
    }

    private CouponResponseDTO convertToDTO(Coupons coupon) {
        return new CouponResponseDTO(
                coupon.getCoupon_id(),
                coupon.getBrand().getBrandName(),
                coupon.getBrandLogo(),
                coupon.getCategory(),
                coupon.getOfferDetails(),
                coupon.getCouponType(),
                coupon.getQuantity(),
                coupon.getPrice(),
                coupon.getExpiryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }
}
