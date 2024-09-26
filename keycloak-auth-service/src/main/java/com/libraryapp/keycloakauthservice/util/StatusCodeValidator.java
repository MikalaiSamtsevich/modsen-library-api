package com.libraryapp.keycloakauthservice.util;

import jakarta.ws.rs.core.Response;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

public class StatusCodeValidator {
    public static final Set<Integer> VALID_STATUSES = Set.of(200, 201, 204, 209);

    private StatusCodeValidator() {
    }

    public static void validate(Response response) {
        if (!VALID_STATUSES.contains(response.getStatus())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(response.getStatus()));
        }
    }
}
