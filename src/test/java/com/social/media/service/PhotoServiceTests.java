package com.social.media.service;

import com.social.media.model.entity.Photo;
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
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class PhotoServiceTests {
    private final PhotoService photoService;
    private Set<Photo> photos;

    @Autowired
    public PhotoServiceTests(PhotoService photoService) {
        this.photoService = photoService;
    }

    @BeforeEach
    public void setPhotos() {
        photos = photoService.getAll();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(photoService).isNotNull();
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
        expected.setFile(new File("photos/nature-photography.webp"));
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
        photoService.delete(1L);

        assertTrue(photos.size() > photoService.getAll().size(),
                "After deleting photos service collection must be smaller than before deleting");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> photoService.delete(0L),
                "Here must be EntityNotFoundException because we have not photo with id 0!");
    }
}
