package com.casestudy.inventory_service.dto;

import lombok.Data;

@Data
public class PayRequestDTO {
    private int amount;
    private int quantity;
    private String email;
    private String receipt;
}
