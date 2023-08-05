package com.social.media.service.authorization;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthPhotoServiceTests {
    private final AuthPhotoService authPhotoService;

    @Autowired
    public AuthPhotoServiceTests(AuthPhotoService authPhotoService) {
        this.authPhotoService = authPhotoService;
    }

    @Test
    public void test_InjectedComponent() {
        AssertionsForClassTypes.assertThat(authPhotoService).isNotNull();
    }

    @Test
    public void test_IsUserOwnerOfPostAndPostContainPhotoWithoutAdmin_True() {
        long ownerId = 3L;
        long postId = 2L;
        long photoId = 3L;

        assertTrue(authPhotoService.isUserOwnerOfPostAndPostContainPhotoWithoutAdmin(ownerId, postId, photoId),
                "Here must be true, because user is owner of post and post contain this photo");
    }

    @Test
    public void test_IsUserOwnerOfPostAndPostContainPhotoWithoutAdmin_False_Post() {
        long ownerId = 3L;
        long postId = 1L;
        long photoId = 3L;

        assertFalse(authPhotoService.isUserOwnerOfPostAndPostContainPhotoWithoutAdmin(ownerId, postId, photoId),
                "Here must be false, because user is not owner of post and post not contain this photo");
    }

    @Test
    public void test_IsUserOwnerOfPostAndPostContainPhotoWithoutAdmin_False_Owner() {
        long ownerId = 2L;
        long postId = 2L;
        long photoId = 3L;

        assertFalse(authPhotoService.isUserOwnerOfPostAndPostContainPhotoWithoutAdmin(ownerId, postId, photoId),
                "Here must be false, because user is not owner of post.");
    }

    @Test
    public void test_IsUserOwnerOfPostAndPostContainPhotoWithoutAdmin_False_Photo() {
        long ownerId = 2L;
        long postId = 3L;
        long photoId = 3L;

        assertFalse(authPhotoService.isUserOwnerOfPostAndPostContainPhotoWithoutAdmin(ownerId, postId, photoId),
                "Here must be false, because post is not contain photo.");
    }

    @Test
    public void test_isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin_True() {
        String username = "oil";
        long ownerId = 3L;
        long postId = 2L;
        long photoId = 3L;

        assertTrue(authPhotoService.isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin(username, ownerId, postId, photoId),
                "Here must be true, because auth user the same with user and user is owner of post and post contain this photo");
    }

    @Test
    public void test_isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin_False_Username() {
        String username = "garry.potter";
        long ownerId = 3L;
        long postId = 2L;
        long photoId = 3L;

        assertFalse(authPhotoService.isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin(username, ownerId, postId, photoId),
                "Here must be false, because auth user is not same with user.");
    }

    @Test
    public void test_isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin_False_OwnerId() {
        String username = "oil";
        long ownerId = 2L;
        long postId = 2L;
        long photoId = 3L;

        assertFalse(authPhotoService.isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin(username, ownerId, postId, photoId),
                "Here must be false, because auth user is not same with user and user is not owner of post.");
    }

    @Test
    public void test_isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin_False_PostId() {
        String username = "oil";
        long ownerId = 3L;
        long postId = 1L;
        long photoId = 3L;

        assertFalse(authPhotoService.isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin(username, ownerId, postId, photoId),
                "Here must be false, because user is not owner of post and post is not contain this photo.");
    }

    @Test
    public void test_isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin_False_PhotoId() {
        String username = "oil";
        long ownerId = 3L;
        long postId = 2L;
        long photoId = 1L;

        assertFalse(authPhotoService.isAuthAndUserOwnerOfPostAndPostContainPhotoWithoutAdmin(username, ownerId, postId, photoId),
                "Here must be false, because post is not contain this photo.");
    }
}
