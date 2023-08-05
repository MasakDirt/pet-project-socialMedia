package com.social.media.service.authorization;

import com.social.media.model.entity.Like;
import com.social.media.service.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthLikeService {
    private final LikeService likeService;
    private final AuthUserService authUserService;
    private final AuthPostService authPostService;

    public boolean isUsersSameAndOwnerOfPostAndPostContainLikeWithoutAdmin(long ownerId, long postId, long likeId, String currentUsername) {
        return authUserService.getUser(currentUsername).getId() == getLike(likeId).getOwner().getId()
                && authPostService.isUserOwnerOfPostWithoutAdmin(ownerId, postId) && getLike(likeId).getPost().getId() == postId;
    }

    public Like getLike(long likeId) {
        return likeService.readById(likeId);
    }
}
