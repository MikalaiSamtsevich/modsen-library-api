package com.libraryapp.keycloakauthservice.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestApi {
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/user")
    public String forUser() {
        return "Im user";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin")
    public String forAdmin() {
        return "Im admin";
    }
}
