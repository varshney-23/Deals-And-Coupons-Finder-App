package com.casestudy.inventory_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaidCouponDTO {

    @NotBlank(message = "brand name can not be blank")
    private String brandName;

    @NotNull
    private String brandLogo;

    @NotNull
    private String category;

    @NotNull
    @Size(max = 255)
    private String offerDetails;

    @Min(0)
    private int quantity;

    @Min(value = 1, message = "Price must be greater than 0")
    private int price;

    @Future
    private LocalDateTime expiryTime;
}
