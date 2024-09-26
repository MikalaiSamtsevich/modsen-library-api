package com.libraryapp.keycloakauthservice.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class KeycloakConfig {


    @Value("${app.keycloak.admin.client-id}")
    private String clientId;

    @Value("${app.keycloak.admin.client-secret}")
    private String clientSecret;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.server-url}")
    private String serverUrl;

    @Value("${app.keycloak.grant-type}")
    private String grantType;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .clientSecret(clientSecret)
                .clientId(clientId)
                .grantType(grantType)
                .realm(realm)
                .serverUrl(serverUrl)
                .build();
    }

    @Bean
    public UserRepresentation userRepresentation() {
        return new UserRepresentation();
    }

    @Bean
    public CredentialRepresentation credentialRepresentation() {
        return new CredentialRepresentation();
    }

    @Bean
    public UsersResource getUsersResource(Keycloak keycloak) {
        return keycloak.realm(realm).users();
    }

    @Bean
    public RolesResource rolesResource(Keycloak keycloak) {
        return keycloak.realm(realm).roles();
    }

    @Bean
    public GroupsResource groupsResource(Keycloak keycloak) {
        return keycloak.realm(realm).groups();
    }

    @Bean
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        return new JwtGrantedAuthoritiesConverter();
    }

}
