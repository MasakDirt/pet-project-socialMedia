package com.social.media.model;

import com.social.media.model.entity.Like;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static com.social.media.model.ValidatorHelperForTests.getViolations;

@SpringBootTest
public class LikeTests {
    private static Like validLike;

    @BeforeAll
    public static void init() {
        validLike = new Like();
        validLike.setPost(new Post());
        validLike.setOwner(new User());
    }

    @Test
    public void test_Valid_Like(){
        Set<ConstraintViolation<Like>> violations = getViolations(validLike);

        Assertions.assertEquals(0, violations.size());
    }
}
