package com.mealmate.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String name;
    private String email;
    private String password;
    private String role; // "PROVIDER" or "CONSUMER"
}
