package com.libraryapp.keycloakauthservice.service;

import com.libraryapp.keycloakauthservice.model.UserLoginRecord;
import org.keycloak.representations.AccessTokenResponse;

public interface UserService {

    AccessTokenResponse getJwt(UserLoginRecord userLoginRecord);

}
