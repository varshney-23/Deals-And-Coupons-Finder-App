package com.casestudy.inventory_service.repository;

import com.casestudy.inventory_service.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BookingRepository  extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    @Query("SELECT s FROM Booking s WHERE s.userId = :user_id AND s.coupon.coupon_id = :coup_id")
    Optional<Booking> findByUserIdAndCouponId(@Param("user_id") long userId,@Param("coup_id") Long couponId);
}
