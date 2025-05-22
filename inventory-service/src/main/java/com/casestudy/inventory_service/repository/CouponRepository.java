package com.casestudy.inventory_service.repository;

import com.casestudy.inventory_service.dto.CouponResponseDTO;
import com.casestudy.inventory_service.model.Brand;
import com.casestudy.inventory_service.model.Coupons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupons, Long> {

    @Query("SELECT new com.casestudy.inventory_service.dto.CouponResponseDTO(" +
            "c.coupon_id, b.brandName, b.brandLogo, c.category, c.offerDetails, " +
            "c.couponType, c.quantity, c.price, CONCAT(c.expiryTime, '') ) " +
            "FROM Coupons c JOIN c.brand b WHERE b = :brand")
    List<CouponResponseDTO> findByBrand(@Param("brand") Brand brand);

    Optional<Coupons> findByBrandAndOfferDetailsIgnoreCase(Brand brand, String offerDetails);

    List<Coupons> findByExpiryTimeBefore(LocalDateTime time);

    List<Coupons> findByCategory(String category);
}
