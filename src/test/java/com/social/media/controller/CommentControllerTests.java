package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.comment.CommentResponseForOwner;
import com.social.media.model.dto.comment.CommentResponseForPost;
import com.social.media.model.entity.Comment;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import com.social.media.model.mapper.CommentMapper;
import com.social.media.service.CommentService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CommentControllerTests {
    private static final String BASE_URL = "/api";

    private final MockMvc mvc;
    private final CommentService commentService;
    private final CommentMapper mapper;
    private final PostService postService;
    private final UserService userService;

    private String tokenAdmin;
    private String tokenUser;

    @Autowired
    public CommentControllerTests(MockMvc mvc, CommentService commentService, CommentMapper mapper,
                                  PostService postService, UserService userService) {
        this.mvc = mvc;
        this.commentService = commentService;
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
                        asJsonString(new LoginRequestWithEmail("garry@mail.co", "2222"))
                )
        ).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(mvc).isNotNull();
        assertThat(commentService).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(postService).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    public void test_Valid_GetAllCommentsUnderPost_AdminAuth() throws Exception {
        long ownerId = 2L;
        long postId = 5L;

        List<CommentResponseForPost> expected = commentService.getAllByPostId(postId)
                .stream()
                .map(mapper::createCommentResponseForPostFromComment)
                .toList();

        mvc.perform(get(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments", ownerId, postId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected).substring(0, 30),
                                result.getResponse().getContentAsString().substring(0, 30),
                                "After reading comment by post id, lists must be sames.")
                );
    }

    @Test
    public void test_Invalid_GetAllCommentsUnderPost_UserAuth_UserNotOwnerOfPost() throws Exception {
        long ownerId = 3L;
        long postId = 3L;

        mvc.perform(get(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments", ownerId, postId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetAllUserComments_AdminAuth() throws Exception {
        long ownerId = 2L;

        List<CommentResponseForOwner> expected = commentService.getAllByOwnerId(ownerId)
                .stream()
                .map(mapper::createCommentResponseForOwnerFromComment)
                .toList();

        mvc.perform(get(BASE_URL + "/users/{owner-id}/comments", ownerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected).substring(0, 25),
                                result.getResponse().getContentAsString().substring(0, 25),
                                "After reading comment by owner id, lists must be sames.")
                );
    }

    @Test
    public void test_Invalid_GetAllUserComments_UserAuth_UsersNotSame() throws Exception {
        long ownerId = 1L;

        mvc.perform(get(BASE_URL + "/users/{owner-id}/comments", ownerId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_PostComment_UserAuth() throws Exception {
        long ownerId = 2L;
        long postId = 5L;
        User user = userService.readByUsername("garry.potter");
        Post post = postService.readById(postId);

        List<Comment> comments = commentService.getAllByPostId(postId);

        mvc.perform(post(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments", ownerId, postId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("comment", "Nice photo!)")
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals(String.format("User %s comment successfully set for %s post.",
                                        user.getName(), post.getOwner().getName()),
                                result.getResponse().getContentAsString(),
                                "After creating new comment user must get this message!")
                );

        assertTrue(comments.size() < commentService.getAllByPostId(postId).size(),
                "List of comments after creating must be bigger!");
    }

    @Test
    public void test_Invalid_PostComment_AdminAuth_UserIsNotOwnerOfPost() throws Exception {
        long invalidOwnerId = 2L;
        long postId = 1L;

        mvc.perform(post(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments", invalidOwnerId, postId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("comment", "comment")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_UpdateComment_AdminAuth() throws Exception {
        long ownerId = 3L;
        long postId = 2L;
        long commentId = 3L;
        String updatedComment = "update";
        Comment unexpected = commentService.readById(commentId);

        mvc.perform(put(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments/{id}", ownerId, postId, commentId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("comment", updatedComment)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(String.format("User %s comment successfully updated for %s post",
                                        unexpected.getOwner().getName(), unexpected.getPost().getOwner().getName()),
                                result.getResponse().getContentAsString(),
                                "After updating user must get this messge!")
                );

        assertEquals(updatedComment, commentService.readById(commentId).getComment());
    }

    @Test
    public void test_Invalid_UpdateComment_UserAuth_UserIsNotOwnerOfPost() throws Exception {
        long ownerId = 2L;
        long postId = 1L;
        long commentId = 1L;

        mvc.perform(put(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments/{id}", ownerId, postId, commentId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("comment", "updatedComment")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_UpdateComment_AdminAuth_AuthUserIsNotSameWithOwner() throws Exception {
        long invalidOwnerId = 3L;
        long postId = 2L;
        long commentId = 2L;

        mvc.perform(put(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments/{id}", invalidOwnerId, postId, commentId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("comment", "updatedComment")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_UpdateComment_UserAuth_PostIsNotContainComment() throws Exception {
        long ownerId = 2L;
        long postId = 3L;
        long commentId = 1L;

        mvc.perform(put(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments/{id}", ownerId, postId, commentId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("comment", "updatedComment")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_DeleteComment_UserAuth() throws Exception {
        long ownerId = 2L;
        long postId = 3L;
        long commentId = 4L;
        Comment comment = commentService.readById(commentId);

        List<Comment> beforeDeleting = commentService.getAllByPostId(postId);

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments/{id}", ownerId, postId, commentId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(String.format("User %s comment successfully deleted for %s post", comment.getOwner().getName(), comment.getPost().getOwner().getName()),
                                result.getResponse().getContentAsString(),
                                "After deleting user must get this message!")
                );

        assertTrue(beforeDeleting.size() > commentService.getAllByPostId(postId).size(),
                "After deleting list size must be smaller than before.");
    }

    @Test
    public void test_Invalid_DeleteComment_UserAuth_UserIsNotOwnerOfPost() throws Exception {
        long ownerId = 2L;
        long postId = 1L;
        long likeId = 1L;

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments/{id}", ownerId, postId, likeId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_DeleteComment_AdminAuth_AuthUserIsNotSameWithOwner() throws Exception {
        long invalidOwnerId = 2L;
        long postId = 3L;
        long commentId = 4L;

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments/{id}", invalidOwnerId, postId, commentId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_DeleteComment_UserAuth_PostIsNotContainComment() throws Exception {
        long ownerId = 2L;
        long postId = 3L;
        long commentId = 1L;

        mvc.perform(delete(BASE_URL + "/users/{owner-id}/posts/{post-id}/comments/{id}", ownerId, postId, commentId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("Access Denied")
                );
    }
}
