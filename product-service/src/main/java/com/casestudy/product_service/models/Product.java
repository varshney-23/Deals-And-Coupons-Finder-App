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

    private String name;
    private double price;
    private double rating;
    private String imageUrl;
    private String category;

    @Override
    public String toString() {
        return "Products [id=" + id + ", name=" + name + ", price=" + price + ", rating=" + rating + ", imageUrl="
                + imageUrl + ", category=" + category + "]";
    }
}
