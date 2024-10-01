package com.libraryapp.keycloakauthservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record NewUserRecord(
        @NotBlank(message = "Username is required")
        @Schema(description = "Username of the new user", example = "johndoe")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be at least 8 characters")
        @Schema(description = "Password for the new user", example = "P@ssw0rd")
        String password,

        @NotBlank(message = "Password confirmation is required")
        @Schema(description = "Confirmation of the password", example = "P@ssw0rd")
        String passwordConfirm,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        @Schema(description = "Email address of the new user", example = "johndoe@example.com")
        String email,

        @NotBlank(message = "First name is required")
        @Schema(description = "First name of the new user", example = "John")
        String firstname,

        @NotBlank(message = "Last name is required")
        @Schema(description = "Last name of the new user", example = "Doe")
        String lastname) {
}