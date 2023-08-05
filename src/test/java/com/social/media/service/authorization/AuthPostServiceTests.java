package com.social.media.service.authorization;

import com.social.media.model.entity.Post;
import com.social.media.service.PostService;
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
public class AuthPostServiceTests {
    private final AuthPostService authPostService;
    private final PostService postService;

    @Autowired
    public AuthPostServiceTests(AuthPostService authPostService, PostService postService) {
        this.authPostService = authPostService;
        this.postService = postService;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(authPostService).isNotNull();
        AssertionsForClassTypes.assertThat(postService).isNotNull();
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin_True() {
        long ownerId = 3L;
        long postId = 2L;
        String authUsername = "oil";

        assertTrue(authPostService.isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin(ownerId, postId, authUsername),
                "Here must be true, because user owner of post and auth user is the same with user id.");
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin_False_IsNotOwner() {
        long ownerId = 3L;
        long postId = 3L;
        String authUsername = "oil";

        assertFalse(authPostService.isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin(ownerId, postId, authUsername),
                "Here must be false, because user isn`t owner of post.");
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin_False_UserNotSames() {
        long ownerId = 3L;
        long postId = 2L;
        String authUsername = "skallet24";

        assertFalse(authPostService.isAuthAndUserSameAndUserOwnerOfPostWithoutAdmin(ownerId, postId, authUsername),
                "Here must be false, because user with id do not equal to auth user.");
    }
    
    @Test
    public void test_isUserOwnerOfPostWithoutAdmin_True() {
        long ownerId = 2L;
        long postID = 5L;

        assertTrue(authPostService.isUserOwnerOfPostWithoutAdmin(ownerId, postID),
                "Here must be true because user owner of post.");
    }

    @Test
    public void test_isUserOwnerOfPostWithoutAdmin_False() {
        long ownerId = 2L;
        long postID = 4L;

        assertFalse(authPostService.isUserOwnerOfPostWithoutAdmin(ownerId, postID),
                "Here must be false because user is not owner of post.");
    }

    @Test
    public void test_GetPost() {
        long postId = 2L;

        Post expected = postService.readById(postId);
        Post actual = authPostService.getPost(postId);

        assertEquals(expected, actual,
                "As we has the same id, so posts must be the same, too!");
    }
}
