package com.social.media.service;

import com.social.media.model.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorizationService {
    private final UserService userService;

    public boolean isAuthAndUserSame(long id, String currentUsername_Email) {
        return isAdmin(currentUsername_Email) || getUser(currentUsername_Email).getId() == id;
    }

    private boolean isAdmin(String currentUsername_Email) {
        return getUser(currentUsername_Email).getRole().getName().equals("ADMIN");
    }

    private User getUser(String currentUsername_Email) {
        return userService.getUserByUsernameOrEmail(currentUsername_Email);
    }
}
