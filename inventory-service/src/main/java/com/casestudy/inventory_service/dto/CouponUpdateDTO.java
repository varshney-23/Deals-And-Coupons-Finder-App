package com.casestudy.inventory_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponUpdateDTO {

    @NotNull(message = "Coupon ID is required")
    private Long couponId;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "Category is required")
    private String category;

    @NotNull(message = "Offer details are required")
    @Size(max = 255, message = "Offer details must be under 255 characters")
    private String offerDetails;

    @NotNull(message = "Coupon Type is required")
    @Pattern(regexp = "paid|promotional", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Coupon type must be 'paid' or 'promotional'")
    private String couponType;

    @Min(value = 0, message = "Quantity must be 0 or more")
    private int quantity;

    @Min(value = 0, message = "Price must be 0 or more")
    private int price;

    @Future(message = "Expiry time must be in the future")
    private LocalDateTime expiryTime;
}