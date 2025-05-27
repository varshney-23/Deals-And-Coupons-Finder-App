package com.dealsandcoupons.payment_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderResponse {
    private String orderId;
    private String status;
    private Double amount;
//    private String sessionUrl;
}