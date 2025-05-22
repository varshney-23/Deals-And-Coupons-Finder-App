package com.casestudy.inventory_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {
    private Long couponId;
    private String brandName;
    private String brandLogo;
    private String category;
    private String offerDetails;
    private String couponType;
    private int quantity;
    private int price;
    private String expiryTime;
}