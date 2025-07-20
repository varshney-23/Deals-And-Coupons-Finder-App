package com.casestudy.inventory_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class MultiCouponBookingRequestDTO {

    private Long userId;
    private String email;
    private List<CouponBookingData> coupons; // list of couponId and quantity

    @Data
    public static class CouponBookingData {
        private Long couponId;
        private int quantity;
    }
}
