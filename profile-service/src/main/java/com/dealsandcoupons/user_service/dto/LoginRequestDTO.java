package com.dealsandcoupons.user_service.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotNull(message = "email is required")
    @Email(message = "must be a valid email")
    private String email;

    @NotBlank(message="Enter Correct Credentials")
    private String password;


}
