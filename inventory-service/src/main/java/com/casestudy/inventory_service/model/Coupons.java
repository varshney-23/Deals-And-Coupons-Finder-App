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

    @NotNull(message = "Brand Logo url is required")
    private String brandLogo; // brand image

    @NotNull(message = "Cateory is required")
    private String category; // fashion, edu, electronics

    @NotNull(message = "Offer details are required")
    @Size(message = "Offer details must be under 255 characters")
    private String offerDetails; // details of offers

    @NotNull(message = "Coupon Type is required")
    @Pattern(regexp = "paid|promotional", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Coupon type mus be 'paid' or 'promotional' ")
    private String couponType; // paid or promotional

    @Min(value = 0, message = "Quantity must be 0 or more")
    //total no. of coupons for this particular category.
    private int quantity;

    @Min(value = 0, message = "Price must be 0 or more")
    private int price;

    @Future(message = "Expiry time must be in the future")
    private LocalDateTime expiryTime;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    @NotNull(message = "Brand is required")
    private Brand brand; // amazon, flipkart
}
