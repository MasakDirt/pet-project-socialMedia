package com.social.media.service;

import com.social.media.exception.LastPhotoException;
import com.social.media.model.entity.Photo;
import com.social.media.model.entity.Post;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class PhotoServiceTests {
    private final PhotoService photoService;
    private final PostService postService;
    private Set<Photo> photos;

    @Autowired
    public PhotoServiceTests(PhotoService photoService, PostService postService) {
        this.photoService = photoService;
        this.postService = postService;
    }

    @BeforeEach
    public void setPhotos() {
        photos = photoService.getAll();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(photoService).isNotNull();
        assertThat(postService).isNotNull();
        assertThat(photos).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertTrue(photoService.getAll().size() > 0,
                "Photos set must contain bigger photos than 0");
        assertEquals(photos, photoService.getAll());
    }

    @Test
    public void test_Valid_Create() {
        Photo expected = new Photo();
        expected.setFile(new File("photos/catshark.webp"));

        Photo actual = photoService.create(expected);
        expected.setId(actual.getId());

        assertEquals(expected, actual,
                "After creating photo objects should be same");
    }

    @Test
    public void test_Invalid_Create() {
        assertThrows(IllegalArgumentException.class, () -> photoService.create(null),
                "Here must be IllegalArgumentException because we cannot create null photo!");
    }

    @Test
    public void test_Valid_ReadById() {
        Photo expected = new Photo();
        expected.setFile(new File("photos/mcLaren.jpg"));
        expected.setPost(postService.readById(3L));
        expected = photoService.create(expected);

        Photo actual = photoService.readById(expected.getId());

        assertEquals(expected, actual,
                "After reading by id, objects should be same.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> photoService.readById(0L),
                "Here must be EntityNotFoundException because we have not photo with id 0.");
    }

    @Test
    public void test_Valid_Delete() {
        photoService.delete(2L, 2L);

        assertTrue(photos.size() > photoService.getAll().size(),
                "After deleting photos service collection must be smaller than before deleting");
    }

    @Test
    public void test_Invalid_Delete_NotFound() {
        assertThrows(EntityNotFoundException.class, () -> photoService.delete(2L, 0L),
                "Here must be EntityNotFoundException because we have not photo with id 0!");
    }

    @Test
    public void test_Invalid_Delete_LastPhoto() {
        assertThrows(LastPhotoException.class, () -> photoService.delete(4L, 1L),
                "Here must be LastPhotoException because we can not delete last photo from post, in post must be at least ine photo!");
    }

    @Test
    public void test_Valid_GetAllByPost() {
        long postId = 2L;
        Post post = postService.readById(postId);

        List<Photo> actualPhotos = photoService.getAllByPost(postId);

        assertAll(
                () -> assertFalse(actualPhotos.isEmpty(),
                        "Post photos list should contains one post!"),
                () -> assertTrue(actualPhotos.size() < photos.size(),
                        "All photos size must be bigger than photos in the post."),
                () -> assertEquals(actualPhotos.size(), post.getPhotos().size(),
                        "Photos in the post must be the same with all photos which reads by post id.")
        );
    }
}
