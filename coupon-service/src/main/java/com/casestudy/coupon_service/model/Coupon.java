package com.casestudy.coupon_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coupons")
public class Coupon {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    private String brand; // brand name obv
    private String logoUrl; // brand url
    private String category; // like edu, electronics, fashion, video games
    private int count; // total
    private String offer; // description kinda thing
    private String coupType; //purchased or promotional

    @Override
    public String toString() {
        return "Coupon [id=" + couponId + ", brand=" + brand + ", " +
                            "logoUrl=" + logoUrl + ", category=" + category + ", " +
                            "count=" + count + ", offer=" + offer + "," +
                            "coupType=" + coupType + "]";
    }

}
