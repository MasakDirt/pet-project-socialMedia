package com.social.media.service;

import com.social.media.model.entity.Comment;
import com.social.media.model.entity.Like;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorizationService {
    private final UserService userService;
    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;

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
        return getUser(currentUsername).getId() == ownerId && isUserOwnerOfPostWithoutAdmin(ownerId, postId);
    }

    public boolean isUserOwnerOfPostWithoutAdmin(long ownerId, long postId) {
       return getPost(postId).getOwner().getId() == ownerId;
    }

    public boolean isUsersSameAndOwnerOfPostAndPostContainLikeWithoutAdmin(long ownerId, long postId, long likeId,  String currentUsername) {
       return isAuthAndUserSameWithoutAdmin(ownerId, currentUsername) && isUserOwnerOfPostWithoutAdmin(ownerId, postId) && getLike(likeId).getPost().getId() == postId;
    }

    public boolean isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin(long ownerId, long postId, long commentId, String currentUsername) {
       return getUser(currentUsername).getId() == getComment(commentId).getOwner().getId() && isUserOwnerOfPostWithoutAdmin(ownerId, postId) && getComment(commentId).getPost().getId() == postId;
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

    private Like getLike(long likeId) {
        return likeService.readById(likeId);
    }

    private Comment getComment(long commentId) {
        return commentService.readById(commentId);
    }
}
