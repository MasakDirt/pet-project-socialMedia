package com.social.media.repository;

import com.social.media.model.entity.Photo;
import com.social.media.model.entity.Post;
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
public class PhotoRepositoryTests {
    private final PhotoRepository photoRepository;
    private final PostRepository postRepository;

    @Autowired
    public PhotoRepositoryTests(PhotoRepository photoRepository, PostRepository postRepository) {
        this.photoRepository = photoRepository;
        this.postRepository = postRepository;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(photoRepository).isNotNull();
        AssertionsForClassTypes.assertThat(postRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindAllByPostId() {
        long postId = 5L;
        Post post = postRepository.findById(postId).orElse(new Post());

        List<Photo> photos = photoRepository.findAllByPostId(postId);

        assertAll(
                () -> assertFalse(photos.isEmpty(),
                        "Post photos list should contains one post!"),
                () -> assertTrue(photos.size() < photoRepository.findAll().size(),
                        "All photos size must be bigger than photos in the post."),
                () -> assertEquals(photos.size(), post.getPhotos().size(),
                        "Photos in the post must be the same with all photos which reads by post id.")
        );
    }

    @Test
    public void test_Invalid_FindAllByPostId() {
        assertTrue(photoRepository.findAllByPostId(0L).isEmpty(),
                "We have no post with id 0, so here must be empty list.");
    }
}
