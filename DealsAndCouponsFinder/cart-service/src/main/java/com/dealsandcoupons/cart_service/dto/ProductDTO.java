package com.dealsandcoupons.cart_service.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private long id;
    private String name;
    private String type;
    private String companyName;
    private String description;
    private String category;
    private double price;
    private double discount;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private String merchantName;
    private String imageURL;
}
