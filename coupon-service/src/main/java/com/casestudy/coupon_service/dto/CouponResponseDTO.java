package com.casestudy.coupon_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponseDTO {
    private Long couponId;
    private String brand;
    private String logoUrl;
    private String category;
    private int count;
    private String offer;
    private String coupType;
}