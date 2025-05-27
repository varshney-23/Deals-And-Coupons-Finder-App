package com.casestudy.inventory_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "coupons")
public class Coupons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coupon_id;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand; // amazon, flipkart

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    private String brandLogo; // brand image

    private String category; // fashion, edu, electronics

    private String offerDetails; // details of offers

    private String couponType; // paid or promotional

    private int quantity;

    private int price;

    private LocalDateTime expiryTime;
}
