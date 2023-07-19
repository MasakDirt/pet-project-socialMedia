package com.social.media.model;

import com.social.media.model.entity.Comment;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static com.social.media.model.ValidatorHelperForTests.getViolations;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentTests {
    private static Comment valicComment;

    @BeforeAll
    public static void init() {
        valicComment = new Comment();
        valicComment.setComment("Nice!");
        valicComment.setOwner(new User());
        valicComment.setTimestamp(LocalDateTime.now());
        valicComment.setPost(new Post());
    }

    @Test
    public void test_Valid_Comment_Class() {
        Set<ConstraintViolation<Comment>> violations = getViolations(valicComment);

        assertEquals(0, violations.size());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidComments")
    public void test_Invalid_Comment_Field(String comment, String error) {
        Comment invalid = new Comment();
        invalid.setComment(comment);
        invalid.setOwner(new User());
        invalid.setTimestamp(LocalDateTime.now());
        invalid.setPost(new Post());

        Set<ConstraintViolation<Comment>> violations = getViolations(invalid);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    @Test
    public void test_Invalid_Timestamp() {
        Comment invalid = new Comment();
        invalid.setComment("comment");
        invalid.setOwner(new User());
        invalid.setPost(new Post());

        Set<ConstraintViolation<Comment>> violations = getViolations(invalid);

        assertEquals(1, violations.size());
    }

    private static Stream<Arguments> provideInvalidComments() {
        return Stream.of(
                Arguments.of("",""),
                Arguments.of(null,null)
        );
    }
}
