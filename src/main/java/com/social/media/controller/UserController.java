package com.social.media.controller;

import com.social.media.model.dto.user.*;
import com.social.media.model.mapper.UserMapper;
import com.social.media.service.RoleService;
import com.social.media.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Set<UserResponse> getAllUsers(Authentication authentication) {
        var responses = userService.getAll()
                .stream()
                .map(mapper::createUserResponseFromUser)
                .collect(Collectors.toSet());
        log.info("=== GET-USERS === admin - {}", authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/first-name/{first-name}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsersByFirstName(@PathVariable(name = "first-name") String firstName, Authentication authentication) {
        var responses = userService.getAllByFirstName(firstName)
                .stream()
                .map(mapper::createUserResponseFromUser)
                .toList();
        log.info("=== GET-USERS-FIRST-NAME === admin - {}", authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/last-name/{last-name}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsersByLastName(@PathVariable(name = "last-name") String lastName, Authentication authentication) {
        var responses = userService.getAllByLastName(lastName)
                .stream()
                .map(mapper::createUserResponseFromUser)
                .toList();
        log.info("=== GET-USERS-LAST-NAME === admin - {}", authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsersByRole(@PathVariable String role, Authentication authentication) {
        var responses = userService.getAllByRole(role)
                .stream()
                .map(mapper::createUserResponseFromUser)
                .toList();
        log.info("=== GET-USERS-ROLE-NAME === admin - {}", authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthAndUserSame(#id, authentication.principal)")
    public UserResponse getUserById(@PathVariable long id, Authentication authentication) {
        var user = mapper.createUserResponseFromUser(userService.readById(id));
        log.info("=== GET-USER-ID === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return user;
    }

    @GetMapping("/username-email/{username-or-email}")
    @PreAuthorize("@authorizationService.isAuthAndUserSameByUsernameOrEmail(#usernameOrEmail, authentication.principal)")
    public UserResponse getUserByUsername(@PathVariable("username-or-email") String usernameOrEmail, Authentication authentication) {
        var user = mapper.createUserResponseFromUser(userService.getUserByUsernameOrEmail(usernameOrEmail));
        log.info("=== GET-USER-USERNAME-EMAIL === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return user;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createAdmin(@RequestBody @Valid UserCreateRequest createRequest, Authentication authentication) {
        var created = mapper.createUserResponseFromUser(
                userService.create(
                        mapper.createUserFromUserCreateRequest(createRequest), roleService.readByName("ADMIN")
                )
        );
        log.info("=== POST-USER-ADMIN === admin - {}", authentication.getPrincipal());

        return created;
    }

    @PostMapping("/custom-role")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUserWithCustomRole(@RequestBody @Valid UserCreateRequestWithRole createRequestWithRole, Authentication authentication) {
        var created = mapper.createUserResponseFromUser(
                userService.create(
                        mapper.createUserFromUserCreateRequestWithRole(createRequestWithRole), roleService.readByName(createRequestWithRole.getRole())
                )
        );
        log.info("=== POST-USER-CUSTOM_ROLE === admin - {}", authentication.getPrincipal());

        return created;
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthAndUserAndUserRequestSame(#id, #requestWithId.id, authentication.principal)")
    public UserResponse updateFullUserById(@PathVariable long id, @RequestBody @Valid UserUpdateRequest requestWithId, Authentication authentication) {
        var updated = userService.update(mapper.createUserFromUserUpdateRequestById(requestWithId), requestWithId.getOldPassword());
        log.info("=== PUT-USER-ID === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return mapper.createUserResponseFromUser(updated);
    }

    @PutMapping("/username/{username}")
    @PreAuthorize("@authorizationService.isAuthAndUserAndUserRequestByUsernameSame(#username, #requestByUsername.username, authentication.principal)")
    public UserResponse updateFullUserByUsername(@PathVariable String username, @RequestBody @Valid UserUpdateRequest requestByUsername, Authentication authentication) {
        var updated = userService.update(mapper.createUserFromUserUpdateRequestByUsername(requestByUsername), requestByUsername.getOldPassword());
        log.info("=== PUT-USER-USERNAME === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return mapper.createUserResponseFromUser(updated);
    }

    @PutMapping("/email/{email}")
    @PreAuthorize("@authorizationService.isAuthAndUserAndUserRequestByEmailSame(#email, #requestByEmail.email, authentication.principal)")
    public UserResponse updateFullUserByEmail(@PathVariable String email, @RequestBody @Valid UserUpdateRequest requestByEmail, Authentication authentication) {
        var updated = userService.update(mapper.createUserFromUserUpdateRequestByEmail(requestByEmail), requestByEmail.getOldPassword());
        log.info("=== PUT-USER-EMAIL === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return mapper.createUserResponseFromUser(updated);
    }

    @PutMapping("/name/{id}")
    @PreAuthorize("@authorizationService.isAuthAndUserSame(#id, authentication.principal)")
    public UserResponse updateUserNamesById(@PathVariable long id, @RequestBody @Valid UserUpdateNamesRequest namesRequest, Authentication authentication) {
        var updated = mapper.createUserResponseFromUser(userService.updateNamesById(id, namesRequest.getFirstName(), namesRequest.getLastName()));
        log.info("=== PUT-USER-NAMES-ID === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return updated;
    }

    @PutMapping("/name/username-email/{username-or-email}")
    @PreAuthorize("@authorizationService.isAuthAndUserSameByUsernameOrEmail(#usernameOrEmail, authentication.principal)")
    public UserResponse updateUserNamesByUsername(@PathVariable("username-or-email") String usernameOrEmail, @RequestBody @Valid UserUpdateNamesRequest namesRequest, Authentication authentication) {
        var updated = mapper.createUserResponseFromUser(userService.updateNamesByUsernameOrEmail(usernameOrEmail, namesRequest.getFirstName(), namesRequest.getLastName()));
        log.info("=== PUT-USER-NAMES-USERNAME-EMAIL === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return updated;
    }

    @PutMapping("/password/{id}")
    @PreAuthorize("@authorizationService.isAuthAndUserSameWithoutAdmin(#id, authentication.principal)")
    public ResponseEntity<String> updateUserPasswordById(@PathVariable long id, @RequestBody @Valid UserUpdatePasswordRequest passwordRequest, Authentication authentication) {
        var user = userService.updatePasswordById(id, passwordRequest.getOldPassword(), passwordRequest.getNewPassword());
        log.info("=== PUT-USER-PASSWORD-ID === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return ResponseEntity.ok("User " + user.getName() + " successfully update his/her password!");
    }

    @PutMapping("/password/username-email/{username-or-email}")
    @PreAuthorize("@authorizationService.isUserSameByUsernameOrEmail(#usernameOrEmail, authentication.principal)")
    public ResponseEntity<String> updateUserPasswordByUsername(@PathVariable("username-or-email") String usernameOrEmail, @RequestBody @Valid UserUpdatePasswordRequest passwordRequest, Authentication authentication) {
        var user = userService.updatePasswordByUsernameOrEmail(usernameOrEmail, passwordRequest.getOldPassword(), passwordRequest.getNewPassword());
        log.info("=== PUT-USER-PASSWORD-USERNAME-EMAIL === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return ResponseEntity.ok("User " + user.getName() + " successfully update his/her password!");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthAndUserSame(#id, authentication.principal)")
    public ResponseEntity<String> deleteUserById(@PathVariable long id, Authentication authentication) {
        var user = userService.readById(id);
        userService.delete(id);
        log.info("=== DELETE-USER-ID === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return ResponseEntity.ok("User with name " + user.getName() + " successfully deleted!");
    }

    @DeleteMapping("/username-email/{username-or-email}")
    @PreAuthorize("@authorizationService.isAuthAndUserSameByUsernameOrEmail(#usernameOrEmail, authentication.principal)")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable("username-or-email") String usernameOrEmail, Authentication authentication) {
        var user = userService.getUserByUsernameOrEmail(usernameOrEmail);
        userService.delete(usernameOrEmail);
        log.info("=== DELETE-USER-USERNAME-EMAIL === {} - {}", authentication.getAuthorities(), authentication.getPrincipal());

        return ResponseEntity.ok("User with name " + user.getName() + " successfully deleted!");
    }
}
