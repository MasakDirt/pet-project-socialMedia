package com.social.media.service.authorization;

import com.social.media.model.entity.Comment;
import com.social.media.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthCommentService {
    private final CommentService commentService;
    private final AuthUserService authUserService;
    private final AuthPostService authPostService;

    public boolean isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin(long ownerId, long postId, long commentId, String currentUsername) {
        return authUserService.getUser(currentUsername).getId() == getComment(commentId).getOwner().getId()
                && authPostService.isUserOwnerOfPostWithoutAdmin(ownerId, postId) && getComment(commentId).getPost().getId() == postId;
    }

    public Comment getComment(long commentId) {
        return commentService.readById(commentId);
    }
}
