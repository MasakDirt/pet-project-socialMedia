package com.social.media.service;

import com.social.media.exception.LikeAlreadyExistException;
import com.social.media.model.entity.Like;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(value = SpringExtension.class)
public class LikeServiceTests {
    private final LikeService likeService;
    private final UserService userService;
    private final PostService postService;

    private Set<Like> likes;

    @Autowired
    public LikeServiceTests(LikeService likeService, UserService userService, PostService postService) {
        this.likeService = likeService;
        this.userService = userService;
        this.postService = postService;
    }

    @BeforeEach
    public void setUp() {
        likes = likeService.getAll();
    }

    @Test
    public void test_Injected_Component() {
        assertThat(likeService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(postService).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertTrue(likeService.getAll().size() > 0,
                "All likes size must be bigger than 0");
        assertEquals(likeService.getAll(), likes,
                "All likes must be the same!");
    }

    @Test
    public void test_Valid_Create_ByOwnerId() {
        long postId = 1L;
        long ownerId = 2L;
        Like expected = new Like();
        expected.setPost(postService.readById(postId));
        expected.setOwner(userService.readById(ownerId));

        Like actual = likeService.create(ownerId, postId);
        expected.setId(actual.getId());

        assertTrue(likes.size() < likeService.getAll().size(),
                "Size of likes before must be less than after creating");
        assertEquals(expected, actual,
                "Expected and actual likes must be fully equal!");
    }

    @Test
    public void test_Valid_Create_ByOwnerUsername() {
        long postId = 1L;
        String ownerUsername = "skallet24";
        Like expected = new Like();
        expected.setPost(postService.readById(postId));
        expected.setOwner(userService.readByUsername(ownerUsername));

        Like actual = likeService.create(ownerUsername, postId);
        expected.setId(actual.getId());

        assertTrue(likes.size() < likeService.getAll().size(),
                "Size of likes before must be less than after creating");
        assertEquals(expected, actual,
                "Expected and actual likes must be fully equal!");
    }

    @Test
    public void test_Invalid_Create() {
        long postId = 4L;
        long ownerId = 2L;
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> likeService.create(ownerId, 0),
                        "Here must EntityNotFoundException because we have not post with id 0!"),

                () -> assertThrows(EntityNotFoundException.class, () -> likeService.create(0, postId),
                        "Here must EntityNotFoundException because we have not owner(user) with id 0!"),

                () -> assertThrows(LikeAlreadyExistException.class, () -> likeService.create(ownerId, postId),
                        "Here must LikeAlreadyExistException because we already have under this post like!")
        );
    }

    @Test
    public void test_Valid_ReadById() {
        Like expected = likeService.create(1L, 4L);
        Like actual = likeService.readById(expected.getId());

        assertEquals(expected, actual,
                "After reading by id expected and actual objects must be Equal.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> likeService.readById(0L),
                "Here must be EntityNotFoundException because we have not like with id 0!");
    }

    @Test
    public void test_Valid_ReadByOwnerAndPost() {
        User owner = userService.readById(3L);
        Post post = postService.readById(3L);

        Like expected = new Like();
        expected.setOwner(owner);
        expected.setPost(post);


        Like actual = likeService.readByOwnerAndPost(owner, post);
        expected.setId(actual.getId());

        assertEquals(expected, actual,
                "Like that we create should be the same as read!");
    }

    @Test
    public void test_Invalid_ReadByOwnerAndPost() {
        assertThat(likeService.readByOwnerAndPost(userService.readById(1L), postService.readById(4L))).isNull();
    }

    @Test
    public void test_Valid_Delete() {
        likeService.delete(3L);

        assertTrue(likes.size() > likeService.getAll().size(),
                "Likes size before deleting must be bigger than after!");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> likeService.delete(0L),
                "Here must be EntityNotFoundException because we have not like with id 0!");
    }

    @Test
    public void test_True_IsExistLike() {
        User owner = userService.readById(2L);
        Post post = postService.readById(3L);

        assertTrue(likeService.isExistLike(owner, post),
                "Like must be set so here should be true!");
    }

    @Test
    public void test_False_IsExistLike() {
        User owner = userService.readById(3L);
        Post post = postService.readById(5L);

        assertFalse(likeService.isExistLike(owner, post),
                "Like not set so here should be false!");
    }

    @Test
    public void test_Valid_GetAllLikesUnderPost() {
        long postId = 3L;

        Post post = postService.readById(postId);
        List<Like> actualLikes = likeService.getAllLikesUnderPost(postId);

        assertAll(
                () -> assertFalse(actualLikes.isEmpty(),
                        "Post likes list should contains one like!"),
                () -> assertTrue(actualLikes.size() < likes.size(),
                        "All likes size must be bigger than likes under the post."),
                () -> assertEquals(actualLikes.size(), post.getLikes().size(),
                        "Likes under the post must be the same with all likes which reads by post id.")
        );
    }

    @Test
    public void test_Invalid_GetAllLikesUnderPost() {
        assertTrue(likeService.getAllLikesUnderPost(0L).isEmpty(),
                "We have no post with id 0, so here must be empty list.");
    }

    @Test
    public void test_Valid_GetAllOwnerLikes() {
        long ownerId = 1L;

        User owner = userService.readById(ownerId);
        List<Like> actualLikes = likeService.getAllOwnerLikes(ownerId);

        assertAll(
                () -> assertFalse(actualLikes.isEmpty(),
                        "User likes list should contains one like!"),
                () -> assertTrue(actualLikes.size() < likes.size(),
                        "All likes size must be bigger than user likes."),
                () -> assertEquals(actualLikes.size(), owner.getMyLikes().size(),
                        "User likes must be the same with all likes which reads by user id.")
        );
    }

    @Test
    public void test_Invalid_GetAllOwnerLikest() {
        assertTrue(likeService.getAllOwnerLikes(0L).isEmpty(),
                "We have no user with id 0, so here must be empty list.");
    }
}
