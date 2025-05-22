package com.casestudy.inventory_service.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private Long userId;
    private Long couponId;

    @Min(value = 1, message = "must be at least 1 coupon")
    private int quantity;
}