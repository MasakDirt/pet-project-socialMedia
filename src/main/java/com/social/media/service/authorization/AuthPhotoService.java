package com.social.media.service.authorization;

import com.social.media.service.PhotoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthPhotoService {
    private final PhotoService photoService;
    private final AuthPostService authPostService;

    public boolean isUserOwnerOfPostAndPostContainPhotoWithoutAdmin(long ownerId, long postId, long photoId) {
        return authPostService.getPost(postId).getOwner().getId() == ownerId && photoService.readById(photoId).getPost().getId() == postId;
    }

    public boolean isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin(String username, long ownerId, long postId, long photoId) {
        var post = authPostService.getPost(postId);
        return post.getOwner().getUsername().equals(username) && post.getOwner().getId() == ownerId && photoService.readById(photoId).getPost().getId() == postId;
    }
}
