package com.casestudy.inventory_service.dto;


import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String role;

    public Long getId() {
        return this.id;
    }
}
