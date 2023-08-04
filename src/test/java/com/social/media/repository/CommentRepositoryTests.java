package com.social.media.repository;

import com.social.media.model.entity.Comment;
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
public class CommentRepositoryTests {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentRepositoryTests(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(commentRepository).isNotNull();
        AssertionsForClassTypes.assertThat(postRepository).isNotNull();
        AssertionsForClassTypes.assertThat(userRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindAllByPostId() {
        long postId = 3L;
        Post post = postRepository.findById(postId).orElse(new Post());
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        assertAll(
                () -> assertTrue(comments.size() > 0,
                        "Comments under the post must be bigger than 0."),
                () -> assertTrue(comments.size() < commentRepository.findAll().size(),
                        "All comments must be bigger than comments under the post!"),
                () -> assertEquals(post.getComments().size(), comments.size(),
                        "Post comments and repository comments reading under the post must be equal.")
        );
    }

    @Test
    public void test_Invalid_FindAllByPostId() {
        assertTrue(commentRepository.findAllByPostId(0L).isEmpty(),
                "When we reading not existing post, here must be empty list");
    }

    @Test
    public void test_Valid_findAllByOwnerId() {
        long ownerId = 3L;
        User owner = userRepository.findById(ownerId).orElse(new User());
        List<Comment> comments = commentRepository.findAllByOwnerId(ownerId);

        assertAll(
                () -> assertTrue(comments.size() > 0,
                        "User comments must be bigger than 0."),
                () -> assertTrue(comments.size() < commentRepository.findAll().size(),
                        "All comments must be bigger than user comments!"),
                () -> assertEquals(owner.getMyComments().size(), comments.size(),
                        "User comments and repository comments reading must be equal.")
        );
    }

    @Test
    public void test_Invalid_findAllByOwnerId() {
        assertTrue(commentRepository.findAllByOwnerId(0L).isEmpty(),
                "When we reading not existing post, here must be empty list");
    }
}
