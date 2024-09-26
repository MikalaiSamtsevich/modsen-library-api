package com.libraryapp.keycloakauthservice.service;

import com.libraryapp.keycloakauthservice.model.NewUserRecord;
import com.libraryapp.keycloakauthservice.model.UserLoginRecord;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;

public interface UserService {

    UserResource createUser(NewUserRecord newUserRecord);

    AccessTokenResponse getJwt(UserLoginRecord userLoginRecord);

    void sendVerificationEmail(String userId);

    UserResource findUserById(String userId);

    void deleteUser(String userId);
}
