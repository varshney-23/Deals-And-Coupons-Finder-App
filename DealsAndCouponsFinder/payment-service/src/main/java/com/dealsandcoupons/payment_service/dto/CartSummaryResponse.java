package com.dealsandcoupons.payment_service.dto;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSummaryResponse {
    private List<String> productTitles;
    private Double totalAmount;
}