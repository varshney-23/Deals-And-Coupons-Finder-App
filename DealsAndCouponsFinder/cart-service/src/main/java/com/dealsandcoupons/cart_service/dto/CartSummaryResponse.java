package com.dealsandcoupons.cart_service.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSummaryResponse {
    private List<String> productTitles;
    private Double totalAmount;
}