package com.casestudy.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayRequestDTO {
    private int amount;
    private String email;
    private String receipt;
}