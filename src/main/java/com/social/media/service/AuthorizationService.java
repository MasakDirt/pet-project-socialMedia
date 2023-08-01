package com.social.media.service;

import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorizationService {
    private final UserService userService;
    private final PostService postService;

    public boolean isAuthAndUserAndUserRequestSame(long userId, long userRequestId, String currentUsername) {
        return userId == userRequestId && isAuthAndUserSame(userId, currentUsername);
    }

    public boolean isAuthAndUserAndUserRequestByUsernameSame(String username, String requestUsername, String currentUsername) {
        return username.equals(requestUsername) && (isAdmin(currentUsername) || getUser(currentUsername).getUsername().equals(username));
    }

    public boolean isAuthAndUserAndUserRequestByEmailSame(String email, String requestEmail, String currentUsername) {
        return email.equals(requestEmail) && (isAdmin(currentUsername) || getUser(currentUsername).getEmail().equals(email));
    }

    public boolean isAuthAndUserSameWithoutAdmin(long id, String currentUsername) {
        return getUser(currentUsername).getId() == id;
    }

    public boolean isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin(long ownerId, long postId, String currentUsername) {
        return getUser(currentUsername).getId() == ownerId && getPost(postId).getOwner().getId() == ownerId;
    }

    public boolean isAuthAndUserSame(long id, String currentUsername) {
        return isAdmin(currentUsername) || isAuthAndUserSameWithoutAdmin(id, currentUsername);
    }

    public boolean isAuthAndUserSameByUsernameOrEmail(String usernameOrEmail, String currentUsername) {
        return isAdmin(currentUsername) || isUserSameByUsernameOrEmail(usernameOrEmail, currentUsername);
    }

    public boolean isUserSameByUsernameOrEmail(String usernameOrEmail, String currentUsername) {
        return getUser(currentUsername).getUsername().equals(usernameOrEmail)
                || getUser(currentUsername).getEmail().equals(usernameOrEmail);
    }

    private boolean isAdmin(String currentUsername) {
        return getUser(currentUsername).getRole().getName().equals("ADMIN");
    }

    private User getUser(String currentUsername) {
        return userService.readByUsername(currentUsername);
    }
    private Post getPost(long id) {
        return postService.readById(id);
    }
}
