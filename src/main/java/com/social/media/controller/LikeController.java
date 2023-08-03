package com.social.media.controller;

import com.social.media.model.dto.like.LikeResponseForOwner;
import com.social.media.model.dto.like.LikeResponseForPosts;
import com.social.media.model.mapper.LikeMapper;
import com.social.media.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.social.media.controller.ControllerHelper.getRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class LikeController {
    private final LikeService likeService;
    private final LikeMapper mapper;

    @GetMapping("/posts/{post-id}/likes")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<LikeResponseForPosts> getAllLikesUnderPost(@PathVariable("post-id") long postId, Authentication authentication) {
        var responses = likeService.
                getAllLikesUnderPost(postId)
                .stream()
                .map(mapper::createLikeResponseForPostsFromLike)
                .toList();
        log.info("=== GET-POST-ID-LIKES === {} - {}", getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @GetMapping("users/{owner-id}/likes")
    @PreAuthorize("@authUserService.isAuthAndUserSame(#ownerId, authentication.principal)")
    public List<LikeResponseForOwner> getAllUserLikes(@PathVariable("owner-id") long ownerId, Authentication authentication) {
        var responses = likeService
                .getAllOwnerLikes(ownerId)
                .stream()
                .map(mapper::createLikeResponseForOwnerFromLike)
                .toList();
        log.info("=== GET-USER-ID-LIKES === {} - {}", getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @PostMapping("users/{owner-id}/posts/{post-id}/likes")
    @PreAuthorize("@authPostService.isUserOwnerOfPostWithoutAdmin(#ownerId, #postId)")
    public ResponseEntity<String> setLike(@PathVariable("owner-id") long ownerId, @PathVariable("post-id") long postId, Authentication authentication) {
        var created = likeService.create(authentication.getName(), postId);
        log.info("=== POST-USER-ID-POSTS-ID-LIKE === {} - {}", getRole(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.SC_CREATED)
                .body(
                        String.format("User %s like successfully set for %s post.", created.getOwner().getName(), created.getPost().getOwner().getName())
                );
    }

    @DeleteMapping("users/{owner-id}/posts/{post-id}/likes/{id}")
    @PreAuthorize("@authLikeService.isUsersSameAndOwnerOfPostAndPostContainLikeWithoutAdmin(#ownerId, #postId, #id, authentication.principal)")
    public ResponseEntity<String> removeLike(@PathVariable("owner-id") long ownerId, @PathVariable("post-id") long postId,
                                             @PathVariable long id, Authentication authentication) {
        var post = likeService.readById(id);
        likeService.delete(id);
        log.info("=== DELETE-USER-ID-POSTS-ID-LIKE === {} - {}", getRole(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(
                String.format("User %s like successfully removed for %s post.", post.getOwner().getName(), post.getPost().getOwner().getName())
        );
    }
}
