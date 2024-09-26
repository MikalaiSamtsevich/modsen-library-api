package com.libraryapp.keycloakauthservice.service.impl;

import com.libraryapp.keycloakauthservice.service.RoleService;
import com.libraryapp.keycloakauthservice.util.KeycloakRole;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RolesResource rolesResource;

    @Override
    public void assignRole(KeycloakRole keycloakRole, UserResource userResource) {
        var role = rolesResource.get(keycloakRole.getRoleName()).toRepresentation();
        userResource.roles().realmLevel().add(List.of(role));
    }

    /**
     * Removes a specified role from the user.
     *
     * @param keycloakRole The role information to be removed.
     * @param userResource The user resource object from which the role is to be removed.
     */
    @Override
    public void deleteRoleFromUser(KeycloakRole keycloakRole, UserResource userResource) {
        // Get the detailed representation of the role by its name
        var role = rolesResource.get(keycloakRole.getRoleName()).toRepresentation();
        // Remove the specified role from the user resource
        userResource.roles().realmLevel().remove(List.of(role));
    }


    @Override
    public List<RoleRepresentation> getAllUserRoles(UserResource userResource) {
        return userResource.roles().realmLevel().listAll();
    }
}
