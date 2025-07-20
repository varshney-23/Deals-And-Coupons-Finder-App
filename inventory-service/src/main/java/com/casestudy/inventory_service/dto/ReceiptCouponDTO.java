package com.casestudy.inventory_service.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptCouponDTO {

    private Long bookingId;

    @NotNull(message = "UserId is required")
    private Long userId;

    @Min(value = 0, message = "Price should not be negative")
    private int price;

    private String offerDetails;

    private int quantity; // for paid coupons

    private String couponCode; // for promo

    private boolean isPaid;
    private String bookingTime;

    private String brandLogo;

}
