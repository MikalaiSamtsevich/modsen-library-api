package com.libraryapp.keycloakauthservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRecord(
        @NotBlank(message = "Username is required")
        @Schema(description = "Username of the user", example = "johndoe")
        String username,

        @NotBlank(message = "Password is required")
        @Schema(description = "Password of the user (minimum 8 characters)", example = "P@ssw0rd")
        String password) {
}