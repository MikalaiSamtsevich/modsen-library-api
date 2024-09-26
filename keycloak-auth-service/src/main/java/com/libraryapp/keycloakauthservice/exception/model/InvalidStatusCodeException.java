package com.libraryapp.keycloakauthservice.exception.model;

public class InvalidStatusCodeException extends RuntimeException {

    public InvalidStatusCodeException(String message) {
        super(message);
    }

    public InvalidStatusCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}