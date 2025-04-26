package com.casestudy.product_service.dto;


import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ResponseDTO {

    private Long id;
    private String name;
    private String category;
    private double price;

    public ResponseDTO(Long id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }
}

