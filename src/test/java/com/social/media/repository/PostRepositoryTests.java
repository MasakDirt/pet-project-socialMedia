package com.social.media.repository;

import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class PostRepositoryTests {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostRepositoryTests(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(postRepository).isNotNull();
        AssertionsForClassTypes.assertThat(userRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindAllByOwnerId() {
        long ownerId = 3L;
        User owner = userRepository.findById(ownerId).orElse(new User());

        List<Post> posts = postRepository.findAllByOwnerId(ownerId);

        assertAll(
                () -> assertFalse(posts.isEmpty(),
                        "Owner posts list should contains one post!"),
                () -> assertTrue(posts.size() < postRepository.findAll().size(),
                        "All posts size must be bigger than user posts."),
                () -> assertEquals(posts.size(), owner.getMyPosts().size(),
                        "User posts must be the same with all posts which reads by owner id.")
        );
    }

    @Test
    public void test_Invalid_FindAllByOwnerId() {
        assertTrue(postRepository.findAllByOwnerId(0L).isEmpty(),
                "We have no user with id 0, so here must be empty list.");
    }
}
