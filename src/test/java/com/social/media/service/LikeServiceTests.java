package com.social.media.service;

import com.social.media.model.entity.Like;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

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
    }

    @Test
    public void test_Valid_Create() {
        long postId = 4L;
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
    public void test_Invalid_Create() {
        long postId = 4L;
        long ownerId = 2L;
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> likeService.create(ownerId, 0),
                        "Here must EntityNotFoundException because we have not post with id 0!"),

                () -> assertThrows(EntityNotFoundException.class, () -> likeService.create(0, postId),
                        "Here must EntityNotFoundException because we have not owner(user) with id 0!")
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
}
