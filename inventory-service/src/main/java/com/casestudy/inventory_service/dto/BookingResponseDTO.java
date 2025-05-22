package com.casestudy.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long bookingId;
    private String couponBrand;
    private String couponType;
    private String couponCode;
    private int quantity;
    private int price;
    private String email;
    private boolean isPaid;
    private String bookingTime;

    public BookingResponseDTO(Long bookingId, String brandName, String couponType, String couponCode, int quantity, boolean paid, int price , String email) {
        this.bookingId = bookingId;
        this.couponBrand = brandName;
        this.couponType = couponType;
        this.couponCode = couponCode;
        this.quantity = quantity;
        this.price = price;
        this.email = email;
        this.isPaid = paid;
    }
    public BookingResponseDTO(int price, int quantity){
        this.price = price;
        this.quantity = quantity;
    }
}