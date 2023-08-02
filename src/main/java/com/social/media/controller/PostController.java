package com.social.media.controller;

import com.social.media.model.dto.post.PostCreateRequest;
import com.social.media.model.dto.post.PostResponse;
import com.social.media.model.mapper.PostMapper;
import com.social.media.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.social.media.controller.ControllerHelper.getRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;
    private final PostMapper mapper;

    @GetMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public Set<PostResponse> getAllPosts(Authentication authentication) {
        var responses = postService
                .getAll()
                .stream()
                .map(mapper::createPostResponseFromPost)
                .collect(Collectors.toSet());
        log.info("=== GET-POSTS === {} - {}", getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/posts/{id}")
    public PostResponse getPost(@PathVariable long id, Authentication authentication) {
        var response = mapper.createPostResponseFromPost(postService.readById(id));
        log.info("=== GET-POST-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/users/{owner-id}/posts")
    public List<PostResponse> getAllUserPosts(@PathVariable("owner-id") long ownerId, Authentication authentication) {
        var responses = postService
                .getUserPosts(ownerId)
                .stream()
                .map(mapper::createPostResponseFromPost)
                .toList();
        log.info("=== GET-USER-ID-POSTS === {} - {}", getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/users/{owner-id}/posts/{id}")
    public PostResponse getUserPost(@PathVariable("owner-id") long ownerId, @PathVariable long id, Authentication authentication) {
        var response = mapper.createPostResponseFromPost(
                postService.readByOwnerIdAndId(ownerId, id)
        );
        log.info("=== GET-USER-ID-POST-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return response;
    }

    @PostMapping("/users/{owner-id}/posts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authUserService.isAuthAndUserSameWithoutAdmin(#ownerId, authentication.principal)")
    public PostResponse createPost(@PathVariable("owner-id") long ownerId,
                                   @RequestBody @Valid PostCreateRequest createRequest, Authentication authentication) {
        var created = mapper.createPostResponseFromPost(
                postService.create(ownerId, createRequest.getDescription(), createRequest.getPhotos())
        );
        log.info("=== POST-USER-ID-POST === {} - {}", getRole(authentication), authentication.getPrincipal());

        return created;
    }

    @PutMapping("/users/{owner-id}/posts/{id}")
    @PreAuthorize("@authPostService.isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin(#ownerId, #id, authentication.principal)")
    public PostResponse updatePostDesc(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                       @RequestBody @NotEmpty @NotNull @Valid String description, Authentication authentication) {
        var updated = mapper.createPostResponseFromPost(
                postService.update(id, description)
        );
        log.info("=== PUT-USER-ID-POST-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return updated;
    }

    @DeleteMapping("/users/{owner-id}/posts/{id}")
    @PreAuthorize("@authPostService.isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> deletePost(@PathVariable("owner-id") long ownerId, @PathVariable long id, Authentication authentication) {
        var post = postService.readById(id);
        postService.delete(id);
        log.info("=== DELETE-USER-ID-POST-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return ResponseEntity.ok("Post for user " + post.getOwner().getName() + " successfully deleted!");
    }
}
