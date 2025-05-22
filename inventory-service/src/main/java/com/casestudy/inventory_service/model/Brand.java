package com.casestudy.inventory_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brand_id;

    private String brandName;
    private String brandLogo;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Coupons> coupons;

}
