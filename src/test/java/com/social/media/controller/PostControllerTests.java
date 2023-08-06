package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.post.PostCreateRequest;
import com.social.media.model.dto.post.PostResponse;
import com.social.media.model.entity.Post;
import com.social.media.model.mapper.PostMapper;
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
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PostControllerTests {
    private static final String BASE_URL = "/api";

    private final MockMvc mvc;
    private final PostService postService;
    private final UserService userService;
    private final PostMapper mapper;

    private String tokenAdmin;
    private String tokenUser;

    @Autowired
    public PostControllerTests(MockMvc mvc, PostService postService,
                               PostMapper mapper, UserService userService) {
        this.mvc = mvc;
        this.postService = postService;
        this.mapper = mapper;
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
        assertThat(postService).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    public void test_Valid_GetAllPosts_AdminAuth() throws Exception {
        mvc.perform(get(BASE_URL + "/posts")
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                );
    }

    @Test
    public void test_Invalid_GetAllPosts_UserAuth_AccessDenied() throws Exception {
        mvc.perform(get(BASE_URL + "/posts")
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetAllUserPosts_AdminAuth() throws Exception {
        testValidGetAllUserPosts(3L, tokenAdmin);
    }

    @Test
    public void test_Valid_GetAllUserPosts_UserAuth() throws Exception {
        testValidGetAllUserPosts(1L, tokenUser);
    }

    private void testValidGetAllUserPosts(long userId, String token) throws Exception {
        List<PostResponse> expected = postService.getUserPosts(userId)
                .stream()
                .map(mapper::createPostResponseFromPost)
                .toList();

        mvc.perform(get(BASE_URL + "/users/{owner-id}/posts", userId)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected).substring(0, 20), result.getResponse().getContentAsString().substring(0, 20),
                                "Lists must be equal, substring used because description has smiles, that not equal in 'result'.")
                );
    }

    @Test
    public void test_Valid_GetUserPost_AdminAuth() throws Exception {
        testValidGetUserPost(2L, 3L, tokenAdmin);
    }

    @Test
    public void test_Valid_GetUserPost_UserAuth() throws Exception {
        testValidGetUserPost(1L, 1L, tokenUser);
    }

    private void testValidGetUserPost(long ownerId, long postId, String token) throws Exception {
        PostResponse expected = mapper.createPostResponseFromPost(postService.readByOwnerIdAndId(ownerId, postId));

        mvc.perform(get(BASE_URL + "/users/{owner-id}/posts/{id}", ownerId, postId)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected).substring(0, 25),
                                result.getResponse().getContentAsString().substring(0, 25),
                                "Posts must be equal because we read it by sames id, " +
                                        "substring used because description has smiles, that not equal in 'result'.")
                );
    }

    @Test
    public void test_Invalid_GetUserPost_UserAuth_UserIsNotOwnerOfPost() throws Exception {
        long ownerID = 3L;
        long postId = 1L;

        mvc.perform(get(BASE_URL + "/users/{owner-id}/posts/{id}", ownerID, postId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains(String.format("User post with user id: %d, and post id: %d, not found", ownerID, postId))
                );
    }

    @Test
    public void test_Valid_CreatePost_AdminAuth() throws Exception {
        testValidCreatePost(1L, tokenAdmin);
    }

    @Test
    public void test_Valid_CreatePost_UserAuth() throws Exception {
        testValidCreatePost(3L, tokenUser);
    }

    private void testValidCreatePost(long ownerId, String token) throws Exception {
        String description = "description";
        List<String> photos = List.of("photos/mcLaren.jpg");

        mvc.perform(post(BASE_URL + "/users/{owner-id}/posts", ownerId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new PostCreateRequest(description, photos))
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains(description)
                );
    }

    @Test
    public void test_Invalid_CreatePost_AdminAuth_UsersNotSame() throws Exception {
        mvc.perform(post(BASE_URL + "/users/{owner-id}/posts", 2L)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new PostCreateRequest("description", List.of("photos/small_cat.jpg")))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_UpdatePost_UserAuth() throws Exception {
        long ownerId = 3L;
        long postId = 2L;
        String newDescription = "New description";

        PostResponse unexpected = mapper.createPostResponseFromPost(postService.readById(postId));

        mvc.perform(put(BASE_URL + "/users/{owner-id}/posts/{id}", ownerId, postId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDescription)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString())
                );
        Post afterUpdating = postService.readById(postId);

        assertEquals(newDescription, afterUpdating.getDescription(),
                "Descriptions that updated must be equal with that we pass to update.");
        assertEquals(unexpected.getPhotos(), afterUpdating.getPhotos(),
                "Photos must not be different after updating.");
    }

    @Test
    public void test_Invalid_UpdatePost_AdminAuth_UsersNotSames() throws Exception {
        mvc.perform(put(BASE_URL + "/users/{owner-id}/posts/{id}", 2L, 3L)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString("users not same"))
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_UpdatePost_UserAuth_UserNotOwnerOfPost() throws Exception {
        mvc.perform(put(BASE_URL + "/users/{owner-id}/posts/{id}", 3L, 3L)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString("users not owner of post"))
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_DeletePost_AdminAuth() throws Exception {
        long ownerId = 1L;
        long postId = 4L;
        Post post = postService.readById(postId);
        List<Post> beforeDeleting = postService.getAll();

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{id}", ownerId, postId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                "Post for user " + post.getOwner().getName() + " successfully deleted!",
                                "After deleting user must get this message!")
                );

        assertTrue(beforeDeleting.size() > postService.getAll().size(),
                "Size of all posts before deleting must be bigger!");
    }

    @Test
    public void test_Invalid_DeletePost_UserAuth_UsersNotSames() throws Exception {
        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{id}", 1L, 4L)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString("users not same"))
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_DeletePost_AdminAuth_UserNotOwnerOfPost() throws Exception {
        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{id}", 1L, 3L)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString("users not owner of post"))
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }
}
