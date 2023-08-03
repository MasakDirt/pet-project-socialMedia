package com.social.media.controller;

import com.social.media.model.dto.photo.PhotoResponse;
import com.social.media.model.mapper.PhotoMapper;
import com.social.media.service.PhotoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.social.media.controller.ControllerHelper.getRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/posts/{post-id}/photos")
public class PhotoController {
    private final PhotoService photoService;
    private final PhotoMapper mapper;


    @GetMapping
    @PreAuthorize("@authPostService.isUserOwnerOfPostWithoutAdmin(#ownerId, #postId)")
    public List<PhotoResponse> getAllPhotosUnderPost(@PathVariable("owner-id") long ownerId, @PathVariable("post-id") long postId,
                                                     Authentication authentication) {
        var responses = photoService
                .getAllByPost(postId)
                .stream()
                .map(mapper::createPhotoResponseFromPhoto)
                .toList();
        log.info("=== GET-USERS-ID-POSTS-ID-PHOTOS === {} - {}", getRole(authentication), authentication.getPrincipal());

        return responses;
    }


    @GetMapping("/{id}")
    @PreAuthorize("@authPhotoService.isUserOwnerOfPostAndPostContainPhotoWithoutAdmin(#ownerId, #postId, #id)")
    public PhotoResponse getPhotoUnderPost(@PathVariable("owner-id") long ownerId, @PathVariable("post-id") long postId,
                                           @PathVariable long id, Authentication authentication) {
        var response = mapper.createPhotoResponseFromPhoto(photoService.readById(id));
        log.info("=== GET-USERS-ID-POSTS-ID-PHOTO-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authPhotoService.isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin(authentication.principal, #ownerId, #postId, #id)")
    public ResponseEntity<String> deletePhotoUnderPost(@PathVariable("owner-id") long ownerId, @PathVariable("post-id") long postId,
                                                       @PathVariable long id, Authentication authentication) {
        var photo = photoService.readById(id);
        photoService.delete(postId, id);
        log.info("=== DELETE-USERS-ID-POSTS-ID-PHOTO-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(
                String.format("Photo in - %s post - '%s' successfully deleted",
                        photo.getPost().getOwner().getName(), photo.getPost().getDescription())
        );
    }
}
