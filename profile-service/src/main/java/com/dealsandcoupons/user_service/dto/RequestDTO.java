package com.dealsandcoupons.user_service.dto;


import com.dealsandcoupons.user_service.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class RequestDTO {

    @NotNull(message="First name is required")
    private String username;

    @NotNull(message = "email is required")
    @Email(message = "must be a valid email")
    private String email;

    @NotNull(message="password is required")
    @Pattern(regexp = "[a-zA-Z0-9!@#$%^&*(){}<>,.?]{8,12}", message = "password must be 8 characters long")
    private String password;

    private Role role;

    public RequestDTO() {

    }
}
