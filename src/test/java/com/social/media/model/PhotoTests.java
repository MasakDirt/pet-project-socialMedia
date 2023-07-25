package com.social.media.model;

import com.social.media.model.entity.Photo;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Set;

import static com.social.media.model.ValidatorHelperForTests.getViolations;

@SpringBootTest
public class PhotoTests {
    private static Photo validPhoto;

    @BeforeAll
    public static void init() {
        validPhoto = new Photo();
        validPhoto.setFile(new File("/photos"));
    }

    @Test
    public void test_Valid_Photo() {
        Set<ConstraintViolation<Photo>> violations = getViolations(validPhoto);

        Assertions.assertEquals(0, violations.size());
    }

    @Test
    public void test_Invalid_Photo() {
        Photo photo = new Photo();
        photo.setFile(null);

        Set<ConstraintViolation<Photo>> violations = getViolations(photo);
        Assertions.assertEquals(1, violations.size());
    }
}
