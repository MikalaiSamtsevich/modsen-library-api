package com.libraryapp.keycloakauthservice.api;

import com.libraryapp.keycloakauthservice.model.UserLoginRecord;
import com.libraryapp.keycloakauthservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthApi {

    public final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody UserLoginRecord userLoginRecord) {
        return ResponseEntity.ok(userService.getJwt(userLoginRecord));
    }

}

