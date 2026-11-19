package com.elshamy.cartify.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDTO {

    private AuthDTO() {
        throw new UnsupportedOperationException("Utility DTO class cannot be instantiated");
    }

    public record RegisterRequest(
            @NotBlank(message = "First name is required")
            String firstName,

            String lastName,

            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 6, message = "Password must be at least 6 characters")
            String password
    ) {
    }

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {
    }

    public record AuthResponse(
            String token,
            String email,
            String role
    ) {
    }
}