package com.libraryapp.keycloakauthservice.api;

import com.libraryapp.keycloakauthservice.service.GroupService;
import com.libraryapp.keycloakauthservice.service.UserService;
import com.libraryapp.keycloakauthservice.util.KeycloakGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupApi {

    private final GroupService groupService;
    private final UserService userService;

    @PutMapping("/{userId}")
    public ResponseEntity<Void> assignGroupToUser(@PathVariable("userId") String userId, @RequestParam("group") KeycloakGroup keycloakGroup) {
        groupService.assignGroupToUser(keycloakGroup, userService.findUserById(userId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<?> deleteGroupFromUser(@PathVariable("user-id") String userId, @RequestParam("group") KeycloakGroup keycloakGroup) {
        groupService.deleteGroupFromUser(keycloakGroup, userService.findUserById(userId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
