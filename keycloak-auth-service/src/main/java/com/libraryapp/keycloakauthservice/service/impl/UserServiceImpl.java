package com.libraryapp.keycloakauthservice.service.impl;

import com.libraryapp.keycloakauthservice.model.NewUserRecord;
import com.libraryapp.keycloakauthservice.model.UserLoginRecord;
import com.libraryapp.keycloakauthservice.service.UserService;
import com.libraryapp.keycloakauthservice.util.KeycloakEvent;
import com.libraryapp.keycloakauthservice.util.StatusCodeValidator;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersResource usersResource;
    private final UserRepresentation userRepresentation;
    private final CredentialRepresentation credentialRepresentation;
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

    @Override
    public UserResource createUser(NewUserRecord newUserRecord) {
        setUserRepresentation(newUserRecord);

        try (var response = usersResource.create(userRepresentation)) {
            log.info("RESPONSE: {}", response.getEntity());
            StatusCodeValidator.validate(response);
            log.info("User {} has been created", newUserRecord.firstname());
        }
        var createdUser = usersResource.search(newUserRecord.username()).get(0);
        sendVerificationEmail(createdUser.getId());
        return findUserById(createdUser.getId());
    }

    @Override
    public void sendVerificationEmail(String userId) {
        usersResource.get(userId).sendVerifyEmail();
    }

    @Override
    public void deleteUser(String userId) {
        try (var response = usersResource.delete(userId)) {
            StatusCodeValidator.validate(response);
        }
    }

    public void setUserRepresentation(NewUserRecord newUserRecord) {
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(newUserRecord.username());
        userRepresentation.setEmail(newUserRecord.email());
        userRepresentation.setFirstName(newUserRecord.username());
        userRepresentation.setLastName(newUserRecord.lastname());
        userRepresentation.setCreatedTimestamp(System.currentTimeMillis());
        userRepresentation.setEmailVerified(false);

        credentialRepresentation.setValue(newUserRecord.password());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));
    }

    @Override
    public UserResource findUserById(String userId) {
        return usersResource.get(userId);
    }

    @Override
    public void forgotPassword(String username) {
        UserResource userResource = usersResource.get(usersResource.search(username).get(0).getId());

        userResource.executeActionsEmail(List.of(KeycloakEvent.UPDATE_PASSWORD.getEvent()));
    }
}
