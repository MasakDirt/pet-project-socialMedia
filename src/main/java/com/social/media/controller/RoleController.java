package com.social.media.controller;

import com.social.media.model.dto.role.RoleResponse;
import com.social.media.model.mapper.RoleMapper;
import com.social.media.service.RoleService;
import com.social.media.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper mapper;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Set<RoleResponse> getAllRoles(Authentication authentication) {
        var responses = roleService.getAll()
                .stream()
                .map(mapper::createRoleResponseFromEntity)
                .collect(Collectors.toSet());

        log.info("=== GET-ROLES === admin - {}", authentication.getPrincipal());
        return responses;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse getRole(@PathVariable long id, Authentication authentication) {
        var role = mapper.createRoleResponseFromEntity(roleService.readById(id));
        log.info("=== GET-ROLE-ID === admin - {}", authentication.getPrincipal());

        return role;
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse getRoleByName(@PathVariable String name, Authentication authentication) {
        var role = mapper.createRoleResponseFromEntity(roleService.readByName(name));
        log.info("=== GET-ROLE-NAME === admin - {}", authentication.getPrincipal());

        return role;
    }

    @GetMapping("/user/{user-id}")
    @PreAuthorize("@authorizationService.isAuthAndUserSame(#id, authentication.principal)")
    public RoleResponse getMyRole(@PathVariable("user-id") long id, Authentication authentication) {
        var userRole = userService.getUserByUsernameOrEmail(authentication.getName()).getRole();

        var responseRole = mapper.createRoleResponseFromEntity(userService.readById(id).getRole());
        log.info("=== GET-USER-ROLE === {} - {}", userRole.getName().toLowerCase(), authentication.getPrincipal());

        return responseRole;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse createNew(@RequestParam(name = "name") String name, Authentication authentication) {
        var created = mapper.createRoleResponseFromEntity(roleService.create(name));
        log.info("=== POST-ROLE === admin - {}", authentication.getPrincipal());

        return created;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse updateRole(@PathVariable long id, @RequestParam(name = "name") String updatedName, Authentication authentication) {
        var updated = mapper.createRoleResponseFromEntity(roleService.update(id, updatedName));
        log.info("=== PUT-ROLE === admin - {}", authentication.getPrincipal());

        return updated;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRole(@PathVariable long id, Authentication authentication) {
        var role = roleService.readById(id);
        roleService.delete(id);
        log.info("=== DELETE-ROLE === admin - {}", authentication.getPrincipal());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        String.format("Role with name '%s' successfully deleted!", role.getName())
                );
    }
}
