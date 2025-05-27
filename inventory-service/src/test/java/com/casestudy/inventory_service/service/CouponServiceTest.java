package com.casestudy.inventory_service.service;

import com.casestudy.inventory_service.Exception.UserException;
import com.casestudy.inventory_service.dto.*;
import com.casestudy.inventory_service.model.Brand;
import com.casestudy.inventory_service.model.Coupons;
import com.casestudy.inventory_service.repository.BrandRepository;
import com.casestudy.inventory_service.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private BrandRepository brandRepository;

    private Brand brand;
    private Coupons coupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        brand = new Brand();
        brand.setBrandName("Nike");
        brand.setBrandLogo("logo.png");

        coupon = new Coupons();
        coupon.setCoupon_id(1L);
        coupon.setBrand(brand);
        coupon.setBrandLogo("logo.png");
        coupon.setCategory("Footwear");
        coupon.setCouponType("paid");
        coupon.setOfferDetails("50% off");
        coupon.setQuantity(100);
        coupon.setPrice(300);
        coupon.setExpiryTime(LocalDateTime.now().plusDays(10));
    }

    @Test
    void testAddPromoCoupon_Success() {
        PromoCouponDTO dto = new PromoCouponDTO();
        dto.setBrandName("Nike");
        dto.setBrandLogo("logo.png");
        dto.setCategory("Shoes");
        dto.setOfferDetails("BOGO");
        dto.setExpiryTime(LocalDateTime.now().plusDays(10));

        when(brandRepository.findByBrandNameIgnoreCase("Nike")).thenReturn(Optional.of(brand));
        when(couponRepository.findByBrandAndOfferDetailsIgnoreCase(any(), any())).thenReturn(Optional.empty());
        when(couponRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<CouponResponseDTO> response = couponService.addPromoCoupon(dto);
        assertEquals("BOGO", response.getBody().getOfferDetails());
        verify(couponRepository).save(any());
    }

    @Test
    void testAddPaidCoupon_Success() {
        PaidCouponDTO dto = new PaidCouponDTO();
        dto.setBrandName("Nike");
        dto.setBrandLogo("logo.png");
        dto.setCategory("Shoes");
        dto.setOfferDetails("50% Off");
        dto.setQuantity(10);
        dto.setPrice(200);
        dto.setExpiryTime(LocalDateTime.now().plusDays(5));

        when(brandRepository.findByBrandNameIgnoreCase("Nike")).thenReturn(Optional.of(brand));
        when(couponRepository.findByBrandAndOfferDetailsIgnoreCase(any(), any())).thenReturn(Optional.empty());
        when(couponRepository.save(any())).thenReturn(coupon);

        ResponseEntity<CouponResponseDTO> response = couponService.addPaidCoupon(dto);
        assertEquals("paid", response.getBody().getCouponType());
        verify(couponRepository).save(any());
    }

    @Test
    void testGetAllCoupons() {
        when(couponRepository.findAll()).thenReturn(List.of(coupon));
        ResponseEntity<List<CouponResponseDTO>> response = couponService.getAllCoupons();
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetCouponById_Success() {
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        ResponseEntity<CouponResponseDTO> response = couponService.getCouponById(1L);
        assertEquals("paid", response.getBody().getCouponType());
    }

    @Test
    void testGetCouponById_NotFound() {
        when(couponRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> couponService.getCouponById(2L));
    }

    @Test
    void testUpdateCoupon_Success() {
        CouponUpdateDTO dto = new CouponUpdateDTO();
        dto.setCouponId(1L);
        dto.setBrandId(1L);
        dto.setCategory("Updated");
        dto.setCouponType("paid");
        dto.setOfferDetails("New Offer");
        dto.setQuantity(10);
        dto.setPrice(300);
        dto.setExpiryTime(LocalDateTime.now().plusDays(10));

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(couponRepository.save(any())).thenReturn(coupon);

        ResponseEntity<CouponResponseDTO> response = couponService.updateCoupon(dto);
        assertEquals("paid", response.getBody().getCouponType());
        verify(couponRepository).save(any());
    }

    @Test
    void testDeleteCouponById_Success() {
        when(couponRepository.existsById(1L)).thenReturn(true);
        ResponseEntity<String> response = couponService.deleteCouponById(1L);
        assertEquals("Coupon deleted successfully!", response.getBody());
        verify(couponRepository).deleteById(1L);
    }

    @Test
    void testDeleteCouponById_NotFound() {
        when(couponRepository.existsById(2L)).thenReturn(false);
        assertThrows(UserException.class, () -> couponService.deleteCouponById(2L));
    }

    @Test
    void testGetCouponsByBrand_Success() {
        Brand brand = new Brand();
        brand.setBrandName("Nike");

        CouponResponseDTO dto = new CouponResponseDTO();
        dto.setCouponId(1L);
        dto.setBrandName("Nike");
        dto.setCategory("Shoes");
        dto.setOfferDetails("10% OFF");
        dto.setCouponType("Discount");
        dto.setQuantity(100);
        dto.setPrice(2000);
        dto.setExpiryTime("2025-12-31");

        List<CouponResponseDTO> dtoList = Collections.singletonList(dto);

        when(brandRepository.findByBrandNameIgnoreCase("Nike")).thenReturn(Optional.of(brand));
        when(couponRepository.findByBrand(brand)).thenReturn(dtoList);

        ResponseEntity<List<CouponResponseDTO>> response = couponService.getCouponsByBrand("Nike");

        assertEquals(1, response.getBody().size());
        assertEquals("Nike", response.getBody().get(0).getBrandName());
        assertEquals("10% OFF", response.getBody().get(0).getOfferDetails());
    }



    @Test
    void testGetCouponsByBrand_NotFound() {
        when(brandRepository.findByBrandNameIgnoreCase("Adidas")).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> couponService.getCouponsByBrand("Adidas"));
    }

    @Test
    void testGetCouponsByCategory_Success() {
        when(couponRepository.findByCategory("Footwear")).thenReturn(List.of(coupon));
        ResponseEntity<List<CouponResponseDTO>> response = couponService.getCouponsByCategory("Footwear");
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetCouponsByCategory_Empty() {
        when(couponRepository.findByCategory("NonExistent")).thenReturn(Collections.emptyList());
        assertThrows(UserException.class, () -> couponService.getCouponsByCategory("NonExistent"));
    }

    @Test
    void testDeleteExpiredCoupons() {
        Coupons expired = new Coupons();
        expired.setCoupon_id(2L);
        expired.setExpiryTime(LocalDateTime.now().minusDays(1));
        when(couponRepository.findByExpiryTimeBefore(any())).thenReturn(List.of(expired));
        couponService.deleteExpiredCoupons();
        verify(couponRepository).deleteAll(List.of(expired));
    }
}
