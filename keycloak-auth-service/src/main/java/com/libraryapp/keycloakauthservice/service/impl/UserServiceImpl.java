package com.libraryapp.keycloakauthservice.service.impl;

import com.libraryapp.keycloakauthservice.model.UserLoginRecord;
import com.libraryapp.keycloakauthservice.service.UserService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${app.keycloak.server-url}")
    private String serverUrl;

    @Value("${app.keycloak.user.client-id}")
    private String userClientId;

    @Value("${app.keycloak.realm}")
    private String realm;

    public AccessTokenResponse getJwt(UserLoginRecord userLoginRecord) {
        @Cleanup Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(userClientId)
                .username(userLoginRecord.username())
                .password(userLoginRecord.password())
                .build();

        return userKeycloak.tokenManager().getAccessToken(); // Возвращаем JWT токен
    }
}
