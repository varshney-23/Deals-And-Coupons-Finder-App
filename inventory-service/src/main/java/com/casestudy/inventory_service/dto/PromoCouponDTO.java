package com.casestudy.inventory_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromoCouponDTO {

    @NotBlank(message = "brand name can not be blank")
    private String brandName;

    @NotNull
    private String brandLogo;

    @NotNull
    private String category;

    @NotNull
    @Size(max = 255)
    private String offerDetails;

    @Future
    private LocalDateTime expiryTime;
}
