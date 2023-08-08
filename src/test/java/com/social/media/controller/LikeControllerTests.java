package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.like.LikeResponseForOwner;
import com.social.media.model.dto.like.LikeResponseForPosts;
import com.social.media.model.entity.Like;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import com.social.media.model.mapper.LikeMapper;
import com.social.media.service.LikeService;
import com.social.media.service.PostService;
import com.social.media.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.social.media.controller.StaticHelperForMVC.asJsonString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class LikeControllerTests {
    private static final String BASE_URL = "/api";

    private final MockMvc mvc;
    private final LikeService likeService;
    private final LikeMapper mapper;
    private final PostService postService;
    private final UserService userService;

    private String tokenAdmin;
    private String tokenUser;

    @Autowired
    public LikeControllerTests(MockMvc mvc, LikeService likeService, LikeMapper mapper,
                               PostService postService, UserService userService) {
        this.mvc = mvc;
        this.likeService = likeService;
        this.mapper = mapper;
        this.postService = postService;
        this.userService = userService;
    }

    @BeforeEach
    void setUp() throws Exception {
        tokenAdmin = mvc.perform(post("/api/auth/login/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithUsername("skallet24", "1111"))
                )
        ).andReturn().getResponse().getContentAsString();

        tokenUser = mvc.perform(post("/api/auth/login/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithEmail("olivia@mail.co", "3333"))
                )
        ).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(mvc).isNotNull();
        assertThat(likeService).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(postService).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    public void test_Valid_GetAllLikesUnderPost_AdminAuth() throws Exception {
        long ownerId = 2L;
        long postId = 3L;

        List<LikeResponseForPosts> expected = likeService.getAllLikesUnderPost(postId)
                .stream()
                .map(mapper::createLikeResponseForPostsFromLike)
                .toList();

        mvc.perform(get(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes", ownerId, postId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Likes that under same post must be the same!")
                );
    }

    @Test
    public void test_Invalid_GetAllLikesUnderPost_Unauthorized() throws Exception {
        mvc.perform(get(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes", 2L, 3L)
                        .header("Authorization", "Bearer " + "invalid token")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertEquals("Error: Unauthorized (please authorize before going to this URL," +
                                        " go to '/api/auth/login/(username or email)' and authorize there, than return!)",
                                result.getResponse().getErrorMessage(),
                                "Likes that under same post must be the same!")
                );
    }

    @Test
    public void test_Invalid_GetAllLikesUnderPost_UserAuth_UserNotOwnerOfPost() throws Exception {
        long ownerId = 3L;
        long postId = 3L;

        mvc.perform(get(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes", ownerId, postId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetAllUserLikes_UserAuth() throws Exception {
        long ownerId = 3L;

        List<LikeResponseForOwner> expected = likeService.getAllOwnerLikes(ownerId)
                .stream()
                .map(mapper::createLikeResponseForOwnerFromLike)
                .toList();

        mvc.perform(get(BASE_URL + "/users/{owner-id}/likes", ownerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected).substring(0, 25),
                                result.getResponse().getContentAsString().substring(0, 25),
                                "Likes that under same post must be the same!")
                );
    }

    @Test
    public void test_Invalid_GetAllUserLikes_UserAuth_UsersNotSame() throws Exception {
        long ownerId = 2L;

        mvc.perform(get(BASE_URL + "/users/{owner-id}/likes", ownerId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_SetLike_UserAuth() throws Exception {
        long ownerId = 2L;
        long postId = 5L;
        User user = userService.readByUsername("oil");
        Post post = postService.readById(postId);

        mvc.perform(post(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes", ownerId, postId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals(String.format("User %s like successfully set for %s post.", user.getName(), post.getOwner().getName()),
                                result.getResponse().getContentAsString(),
                                "After creating user must get this message!")
                );
    }

    @Test
    public void test_Invalid_SetLike_AdminAuth_UserIsNotOwnerOfPost() throws Exception {
        long invalidOwnerId = 3L;
        long postId = 1L;

        mvc.perform(post(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes", invalidOwnerId, postId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_RemoveLike_UserAuth() throws Exception {
        long ownerId = 2L;
        long postId = 3L;
        long likeId = 7L;
        Like like = likeService.readById(likeId);

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes/{id}", ownerId, postId, likeId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(String.format("User %s like successfully removed for %s post.", like.getOwner().getName(), like.getPost().getOwner().getName()),
                                result.getResponse().getContentAsString(),
                                "After deleting user must get this message!")
                );
    }

    @Test
    public void test_Invalid_RemoveLike_UserAuth_UserIsNotOwnerOfPost() throws Exception {
        long invalidOwnerId = 3L;
        long postId = 1L;
        long likeId = 1L;

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes/{id}", invalidOwnerId, postId, likeId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_RemoveLike_AdminAuth_AuthUserIsNotSameWithOwner() throws Exception {
        long invalidOwnerId = 3L;
        long postId = 2L;
        long likeId = 2L;

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes/{id}", invalidOwnerId, postId, likeId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_RemoveLike_UserAuth_PostIsNotContainLike() throws Exception {
        long invalidOwnerId = 3L;
        long postId = 2L;
        long likeId = 1L;

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{post-id}/likes/{id}", invalidOwnerId, postId, likeId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }
}
