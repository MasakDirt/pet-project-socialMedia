package com.social.media.service.authorization;

import com.social.media.model.entity.Post;
import com.social.media.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthPostService {
    private final PostService postService;
    private final AuthUserService authUserService;

    public boolean isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin(long ownerId, long postId, String currentUsername) {
        return authUserService.getUser(currentUsername).getId() == ownerId && isUserOwnerOfPostWithoutAdmin(ownerId, postId);
    }

    public boolean isUserOwnerOfPostWithoutAdmin(long ownerId, long postId) {
        return getPost(postId).getOwner().getId() == ownerId;
    }

    public Post getPost(long id) {
        return postService.readById(id);
    }
}
