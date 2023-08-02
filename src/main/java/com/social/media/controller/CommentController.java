package com.social.media.controller;

import com.social.media.model.dto.comment.CommentResponseForOwner;
import com.social.media.model.dto.comment.CommentResponseForPost;
import com.social.media.model.mapper.CommentMapper;
import com.social.media.service.CommentService;
import jakarta.validation.constraints.NotBlank;
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
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper mapper;

    @GetMapping("/posts/{post-id}/comments")
    public List<CommentResponseForPost> getAllCommentsUnderPost(@PathVariable("post-id") long postId, Authentication authentication) {
        var responses = commentService
                .getAllByPostId(postId)
                .stream()
                .map(mapper::createCommentResponseForPostFromComment)
                .toList();
        log.info("=== GET-POSTS-ID-COMMENTS === {} - {}", getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/users/{owner-id}/comments")
    @PreAuthorize("@authUserService.isAuthAndUserSame(#ownerId, authentication.principal)")
    public List<CommentResponseForOwner> getAllUserComments(@PathVariable("owner-id") long ownerId, Authentication authentication) {
        var responses = commentService
                .getAllByOwnerId(ownerId)
                .stream()
                .map(mapper::createCommentResponseForOwnerFromComment)
                .toList();
        log.info("=== GET-USERS-ID-COMMENTS === {} - {}", getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @PostMapping("/users/{owner-id}/posts/{post-id}/comments")
    @PreAuthorize("@authPostService.isUserOwnerOfPostWithoutAdmin(#ownerId, #postId)")
    public ResponseEntity<String> postComment(@PathVariable("owner-id") long ownerId, @PathVariable("post-id") long postId,
                                              @NotBlank(message = "Comment cannot be blank!")
                                              @RequestParam String comment, Authentication authentication) {
        var created = commentService.create(authentication.getName(), postId, comment);
        log.info("=== POST-USERS-ID-POSTS-ID-COMMENTS === {} - {}", getRole(authentication), authentication);

        return ResponseEntity.status(HttpStatus.SC_CREATED)
                .body(
                        String.format("User %s comment successfully set for %s post.", created.getOwner().getName(), created.getPost().getOwner().getName())
                );
    }

    @PutMapping("/users/{owner-id}/posts/{post-id}/comments/{id}")
    @PreAuthorize("@authCommentService.isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin(#ownerId, #postId, #id, authentication.principal)")
    public ResponseEntity<String> updateComment(@PathVariable("owner-id") long ownerId, @PathVariable("post-id") long postId, @PathVariable long id,
                                                @NotBlank(message = "Comment cannot be blank!")
                                                @RequestParam("comment") String updatedComment, Authentication authentication) {
        var updated = commentService.update(id, updatedComment);
        log.info("=== PUT-USERS-ID-POSTS-ID-COMMENTS-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(
                String.format("User %s comment successfully updated for %s post", updated.getOwner().getName(), updated.getPost().getOwner().getName())
        );
    }

    @DeleteMapping("/users/{owner-id}/posts/{post-id}/comments/{id}")
    @PreAuthorize("@authCommentService.isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin(#ownerId, #postId, #id, authentication.principal)")
    public ResponseEntity<String> deleteComment(@PathVariable("owner-id") long ownerId, @PathVariable("post-id") long postId,
                                                @PathVariable long id, Authentication authentication) {
        var comment = commentService.readById(id);
        commentService.delete(id);
        log.info("=== DELETE-USERS-ID-POSTS-ID-COMMENTS-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(
                String.format("User %s comment successfully deleted for %s post", comment.getOwner().getName(), comment.getPost().getOwner().getName())
        );
    }
}
