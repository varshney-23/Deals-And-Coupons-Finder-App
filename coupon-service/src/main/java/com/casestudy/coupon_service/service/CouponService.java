package com.casestudy.coupon_service.service;

import com.casestudy.coupon_service.Interface.ICouponService;
import com.casestudy.coupon_service.dto.CouponResponseDTO;
import com.casestudy.coupon_service.dto.ProductRequestDTO;
import com.casestudy.coupon_service.feignClient.ProductClient;
import com.casestudy.coupon_service.model.Coupon;
import com.casestudy.coupon_service.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService implements ICouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProductClient productClient;

    public CouponResponseDTO addCoupon(Coupon coupon) {
        // Pehle Coupon save karo
        Coupon savedCoupon = couponRepository.save(coupon);

        // Fir Product-Service mein brand bhi save karna
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName(coupon.getBrand());
        dto.setLogoUrl(coupon.getLogoUrl());
        dto.setCategory(coupon.getCategory());

        productClient.addBrand(dto);

        return convertToDTO(savedCoupon);
    }

    public CouponResponseDTO updateCoupon(Coupon coupon) {
        Coupon updatedCoupon = couponRepository.save(coupon);
        return convertToDTO(updatedCoupon);
    }

    public void deleteCouponById(Long couponId) {
        couponRepository.deleteById(couponId);
    }

    public void deleteCouponByBrand(String brand) {
        List<Coupon> coupons = couponRepository.findByBrand(brand);
        couponRepository.deleteAll(coupons);
    }

    public List<CouponResponseDTO> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CouponResponseDTO> getCouponsByBrand(String brand) {
        return couponRepository.findByBrand(brand).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CouponResponseDTO> getCouponsByCategory(String category) {
        return couponRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CouponResponseDTO getCouponById(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return convertToDTO(coupon);
    }

    public List<CouponResponseDTO> getCouponsByType(String coupType) {
        return couponRepository.findByCoupType(coupType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CouponResponseDTO convertToDTO(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getCouponId(),
                coupon.getBrand(),
                coupon.getLogoUrl(),
                coupon.getCategory(),
                coupon.getCount(),
                coupon.getOffer(),
                coupon.getCoupType()
        );
    }
}
