package com.social.media.service;

import com.social.media.model.entity.Like;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import com.social.media.repository.LikeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final PostService postService;

    public Like create(long ownerId, long postId) {
        var owner = userService.readById(ownerId);
        var post = postService.readById(postId);

        var like = new Like();
        like.setOwner(owner);
        like.setPost(post);

        return likeRepository.save(like);
    }

    public Like readById(long id) {
        return likeRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Like with id " + id + " not found!"));
    }

    public Like readByOwnerAndPost(@NotNull User owner, @NotNull Post post) {
        return likeRepository.findByOwnerAndPost(owner, post);
    }

    public void delete(long id) {
        likeRepository.delete(readById(id));
    }

    public boolean isExistLike(@NotNull User owner, @NotNull Post post) {
        Like like = readByOwnerAndPost(owner, post);
        return like != null;
    }
}
