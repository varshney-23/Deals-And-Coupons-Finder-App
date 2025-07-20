package com.casestudy.inventory_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @NotNull(message = "UserId is required")
    private Long userId;

    @Min(value = 0, message = "Price should not be negative")
    private int price;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private BookingGroup bookingGroup;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @NotNull(message = "Coupon reference is required")
    private Coupons coupon;

    @Min(value = 1, message = "must be at least 1 coupon")
    //quantity of coupons that has been purchased.
    private int quantity; // for paid coupons

    @Size(max = 12, message = "Coupon code should have at most 12 characters")
    private String couponCode; // for promo

    private String email;

    private boolean isPaid;
    private LocalDateTime bookingTime;
}
