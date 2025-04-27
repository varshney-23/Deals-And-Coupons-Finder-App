package com.casestudy.product_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //brand
    private double rating; // maybe company's rating?
    private String imageUrl; // logo
    private String category; // like edu, fashion, accessories, electronics

    @Override
    public String toString() {
        return "Products [id=" + id + ", name=" + name + ", rating=" + rating + ", imageUrl="
                + imageUrl + ", category=" + category + "]";
    }
}
