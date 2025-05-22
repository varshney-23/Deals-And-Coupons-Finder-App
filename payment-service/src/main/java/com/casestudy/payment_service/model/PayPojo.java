package com.casestudy.payment_service.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "payments")
public class PayPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private String email;
    private String orderStatus;
    private String razorpayOrderId;

}
