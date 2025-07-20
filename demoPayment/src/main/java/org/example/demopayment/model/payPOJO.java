package org.example.demopayment.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "demopaydb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class payPOJO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razorpayOrderId;
    private Double amount;
    private String currency;
    private String status;
    private String receipt;
    private String createdAt;
}
