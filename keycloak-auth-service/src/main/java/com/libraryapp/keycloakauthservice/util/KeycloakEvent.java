package com.libraryapp.keycloakauthservice.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeycloakEvent {
    UPDATE_PASSWORD("UPDATE_PASSWORD");
    private final String event;
}
