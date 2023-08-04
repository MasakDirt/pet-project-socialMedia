package com.social.media.model;

import com.social.media.model.entity.Photo;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.social.media.model.ValidatorHelperForTests.getViolations;

@SpringBootTest
public class PostTests {
    private static Post validPost;

    @BeforeAll
    public static void init() {
        Photo photo = new Photo();
        photo.setFile(new File("photos/nature-photography.webp"));

        validPost = new Post();
        validPost.setPhotos(List.of(photo));
        validPost.setTimestamp(LocalDateTime.now());
        validPost.setOwner(new User());
        validPost.setDescription("Description");
        photo.setPost(validPost);
    }

    @Test
    public void test_Valid_Post() {
        Set<ConstraintViolation<Post>> violations = getViolations(validPost);

        Assertions.assertEquals(0, violations.size());
    }

    @Test
    public void test_Invalid_Timestamp() {
        Post invalid = new Post();
        invalid.setPhotos(new LinkedList<>());
        invalid.setDescription("");
        invalid.setTimestamp(null);

        Set<ConstraintViolation<Post>> violations = getViolations(invalid);
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    public void test_Invalid_Description() {
        Post invalid = new Post();
        invalid.setPhotos(new LinkedList<>());
        invalid.setDescription(null);
        invalid.setTimestamp(LocalDateTime.now());

        Set<ConstraintViolation<Post>> violations = getViolations(invalid);
        Assertions.assertEquals(1, violations.size());
    }
}
