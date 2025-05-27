package com.casestudy.inventory_service.service;

import com.casestudy.inventory_service.Interface.ICouponService;
import com.casestudy.inventory_service.dto.CouponResponseDTO;
import com.casestudy.inventory_service.Exception.UserException;
import com.casestudy.inventory_service.dto.CouponUpdateDTO;
import com.casestudy.inventory_service.dto.PaidCouponDTO;
import com.casestudy.inventory_service.dto.PromoCouponDTO;
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

    public ResponseEntity<CouponResponseDTO> addPromoCoupon(PromoCouponDTO dto) {
        Brand brand = getOrSavedBrand(dto.getBrandName(), dto.getBrandLogo());

        Optional<Coupons> existingCoupon = couponRepository.findByBrandAndOfferDetailsIgnoreCase(
                brand, dto.getOfferDetails()
        );

        if (existingCoupon.isPresent()) {
            throw new IllegalArgumentException("Coupon with the same brand and offer already exists");
        }

        Coupons couponNew = new Coupons();
        couponNew.setBrand(brand);
        couponNew.setBrandLogo(dto.getBrandLogo());
        couponNew.setCategory(dto.getCategory());
        couponNew.setCouponType("promotional");
        couponNew.setCategory(dto.getCategory());
        couponNew.setOfferDetails(dto.getOfferDetails());
        couponNew.setQuantity(1);
        couponNew.setPrice(0);
        couponNew.setExpiryTime(dto.getExpiryTime());

        couponRepository.save(couponNew);
        return ResponseEntity.ok(convertToPromoDTO(couponNew));

    }
    public ResponseEntity<CouponResponseDTO> addPaidCoupon(PaidCouponDTO dto) {
        Brand brand = getOrSavedBrand(dto.getBrandName(), dto.getBrandLogo());

        Optional<Coupons> existingCoupon = couponRepository.findByBrandAndOfferDetailsIgnoreCase(
                brand, dto.getOfferDetails()
        );

        if (existingCoupon.isPresent()) {
            throw new IllegalArgumentException("Coupon with the same brand and offer already exists");
        }

        Coupons couponNew = new Coupons();
        couponNew.setBrand(brand);
        couponNew.setBrandLogo(dto.getBrandLogo());
        couponNew.setCategory(dto.getCategory());
        couponNew.setCouponType("paid");
        couponNew.setCategory(dto.getCategory());
        couponNew.setOfferDetails(dto.getOfferDetails());
        couponNew.setQuantity(dto.getQuantity());
        couponNew.setPrice(dto.getPrice());
        couponNew.setExpiryTime(dto.getExpiryTime());

        Coupons saved = couponRepository.save(couponNew);
        return ResponseEntity.ok(convertToPaidDTO(saved));
    }

    public void deleteExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupons> expired = couponRepository.findByExpiryTimeBefore(now);
        if(!expired.isEmpty()) {
            couponRepository.deleteAll(expired);
            System.out.println("Expired coupons deleted: "+expired.size());
        }
    }

    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        List<CouponResponseDTO> list = couponRepository.findAll().stream()
                .map(this::convertToPaidDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    public ResponseEntity<CouponResponseDTO> getCouponById(Long id) {
        Coupons coupon = couponRepository.findById(id)
                .orElseThrow(() -> new UserException("Coupon not found with ID: " + id));
        return ResponseEntity.ok(convertToPaidDTO(coupon));
    }

    public ResponseEntity<CouponResponseDTO> updateCoupon(CouponUpdateDTO dto) {
        Coupons coupon = couponRepository.findById(dto.getCouponId())
                .orElseThrow(() -> new UserException("Coupon not found"));

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new UserException("Brand not found"));

        coupon.setBrand(brand);
        coupon.setCategory(dto.getCategory());
        coupon.setOfferDetails(dto.getOfferDetails());
        coupon.setCouponType(dto.getCouponType());
        coupon.setQuantity(dto.getQuantity());
        coupon.setPrice(dto.getPrice());
        coupon.setExpiryTime(dto.getExpiryTime());

        Coupons updated = couponRepository.save(coupon);
        return ResponseEntity.ok(convertToPaidDTO(updated));
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
                .map(this::convertToPaidDTO)
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new UserException("No coupons found in category: " + category);
        }
        return ResponseEntity.ok(list);
    }

    private Brand getOrSavedBrand(String brandName, String brandLogo){
        return brandRepository.findByBrandNameIgnoreCase(brandName)
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setBrandName(brandName);
                    newBrand.setBrandLogo(brandLogo);
                    return brandRepository.save(newBrand);
                });
    }

    private CouponResponseDTO convertToPaidDTO(Coupons coupon) {
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

    private CouponResponseDTO convertToPromoDTO(Coupons coupon){
        return new CouponResponseDTO(
                coupon.getCoupon_id(),
                coupon.getCategory(),
                coupon.getOfferDetails(),
                coupon.getCouponType(),
                coupon.getExpiryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }
}
