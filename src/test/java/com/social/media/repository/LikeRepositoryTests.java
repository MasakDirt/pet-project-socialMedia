package com.social.media.repository;

import com.social.media.model.entity.Like;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class LikeRepositoryTests {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public LikeRepositoryTests(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Test
    public void test_InjectedComponent() {
        AssertionsForClassTypes.assertThat(likeRepository).isNotNull();
        AssertionsForClassTypes.assertThat(userRepository).isNotNull();
        AssertionsForClassTypes.assertThat(postRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindByOwnerAndPost() {
        User owner = userRepository.findById(2L).orElse(new User());
        Post post = owner.getMyPosts().stream().findFirst().orElse(new Post());

        Like expected = new Like();
        expected.setPost(post);
        expected.setOwner(owner);

        Like actual = likeRepository.findByOwnerAndPost(owner, post);
        expected.setId(actual.getId());

        assertEquals(expected, actual,
                "Likes after reading by owner and post must be equal!");
    }

    @Test
    public void test_Invalid_FindByOwnerAndPost() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> likeRepository.findByOwnerAndPost(new User(), new Post()),
                "We have no this user and post in db, so here must be InvalidDataAccessApiUsageException!");
    }

    @Test
    public void test_Valid_FindAllByPostId() {
        long postId = 1L;

        Post post = postRepository.findById(postId).orElse(new Post());
        List<Like> likes = likeRepository.findAllByPostId(postId);

        assertAll(
                () -> assertFalse(likes.isEmpty(),
                        "Post likes list should contains one like!"),
                () -> assertTrue(likes.size() < likeRepository.findAll().size(),
                        "All likes size must be bigger than likes under the post."),
                () -> assertEquals(likes.size(), post.getLikes().size(),
                        "Likes under the post must be the same with all likes which reads by post id.")
        );
    }

    @Test
    public void test_Invalid_FindAllByPostId() {
        assertTrue(likeRepository.findAllByPostId(0L).isEmpty(),
                "We have no post with id 0, so here must be empty list.");
    }

    @Test
    public void test_Valid_findAllByOwnerId() {
        long ownerId = 2L;

        User owner = userRepository.findById(ownerId).orElse(new User());
        List<Like> likes = likeRepository.findAllByOwnerId(ownerId);

        assertAll(
                () -> assertFalse(likes.isEmpty(),
                        "User likes list should contains one like!"),
                () -> assertTrue(likes.size() < likeRepository.findAll().size(),
                        "All likes size must be bigger than user likes."),
                () -> assertEquals(likes.size(), owner.getMyLikes().size(),
                        "Likes that user set must be the same likes that reads by repository.")
        );
    }

    @Test
    public void test_Invalid_findAllByOwnerId() {
        assertTrue(likeRepository.findAllByOwnerId(0L).isEmpty(),
                "We have no user with id 0, so here must be empty list.");
    }
}
