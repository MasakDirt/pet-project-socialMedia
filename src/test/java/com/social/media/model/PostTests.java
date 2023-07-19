package com.social.media.model;

import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;

import static com.social.media.model.ValidatorHelperForTests.getViolations;

@SpringBootTest
public class PostTests {
    private static Post validPost;

    @BeforeAll
    public static void init() {
        validPost = new Post();
        validPost.setPhoto(new File("photos/nature-photography.webp"));
        validPost.setTimestamp(LocalDateTime.now());
        validPost.setOwner(new User());
    }

    @Test
    public void test_Valid_Post() {
        Set<ConstraintViolation<Post>> violations = getViolations(validPost);

        Assertions.assertEquals(0, violations.size());
    }

    @Test
    public void test_Invalid_Photo() {
        Post invalid = new Post();
        invalid.setPhoto(null);
        invalid.setTimestamp(LocalDateTime.now());

        Set<ConstraintViolation<Post>> violations = getViolations(invalid);
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    public void test_Invalid_Timestamp() {
        Post invalid = new Post();
        invalid.setPhoto(new File("photos/nature-photography.webp"));
        invalid.setTimestamp(null);

        Set<ConstraintViolation<Post>> violations = getViolations(invalid);
        Assertions.assertEquals(1, violations.size());
    }
}
