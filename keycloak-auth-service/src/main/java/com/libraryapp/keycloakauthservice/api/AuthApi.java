package com.libraryapp.keycloakauthservice.api;

import com.libraryapp.keycloakauthservice.model.NewUserRecord;
import com.libraryapp.keycloakauthservice.model.UserLoginRecord;
import com.libraryapp.keycloakauthservice.service.RoleService;
import com.libraryapp.keycloakauthservice.service.UserService;
import com.libraryapp.keycloakauthservice.util.KeycloakRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
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

    public final RoleService roleService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody @Valid UserLoginRecord userLoginRecord) {
        return ResponseEntity.ok(userService.getJwt(userLoginRecord));
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody @Valid NewUserRecord newUserRecord) {
        var createdUser = userService.createUser(newUserRecord);
        roleService.assignRole(KeycloakRole.USER, createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

