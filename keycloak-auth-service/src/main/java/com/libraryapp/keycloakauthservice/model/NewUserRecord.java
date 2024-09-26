package com.libraryapp.keycloakauthservice.model;

public record NewUserRecord(String username,
                            String password,
                            String passwordConfirm,
                            String email,
                            String firstname,
                            String lastname) {
}
