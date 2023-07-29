package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.user.UserCreateRequest;
import com.social.media.model.dto.user.UserMapper;
import com.social.media.model.dto.user.UserResponse;
import com.social.media.service.RoleService;
import com.social.media.service.UserService;
import com.social.media.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping("/login/username")
    public String login(@RequestBody @Valid LoginRequestWithUsername loginRequest) {
        var userDetails = userService.readByUsername(loginRequest.getUsername());

        matchesPassword(loginRequest.getPassword(), userDetails.getPassword());

        log.info("=== POST-LOGIN-USERNAME === auth.name - {} === time - {}.", userDetails.getUsername(), LocalDateTime.now());

        return jwtUtils.generateTokenFromUsername(userDetails.getUsername());
    }

    @PostMapping("/login/email")
    public String login(@RequestBody @Valid LoginRequestWithEmail loginRequest) {
        var userDetails = userService.readByEmail(loginRequest.getEmail());

        matchesPassword(loginRequest.getPassword(), userDetails.getPassword());

        log.info("=== POST-LOGIN-EMAIL === auth.name - {} === time - {}.", userDetails.getEmail(), LocalDateTime.now());

        return jwtUtils.generateTokenFromUsername(userDetails.getEmail());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createNewCustomer(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        var user = userService.create(mapper.createUserFromUserCreateRequest(userCreateRequest), roleService.readByName("USER"));

        log.info("=== POST-REGISTER === reg.name - {} === time - {}.", user.getUsername(), LocalDateTime.now());
        return mapper.createUserResponseFromUser(user);
    }

    private void matchesPassword(String enteredPassword, String dbPassword) {
        if (!passwordEncoder.matches(enteredPassword, dbPassword)) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Wrong password");
        }
    }
}
