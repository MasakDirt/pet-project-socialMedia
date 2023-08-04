package com.social.media.service.authorization;

import com.social.media.model.entity.Comment;
import com.social.media.service.CommentService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthCommentServiceTests {
    private final AuthCommentService authCommentService;
    private final CommentService commentService;

    @Autowired
    public AuthCommentServiceTests(AuthCommentService authCommentService, CommentService commentService) {
        this.authCommentService = authCommentService;
        this.commentService = commentService;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(authCommentService).isNotNull();
        AssertionsForClassTypes.assertThat(commentService).isNotNull();
    }

    @Test
    public void test_isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin_True() {
        assertTrue(authCommentService.isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin(1L, 1L, 1L, "garry.potter"),
                "Here user owner of post and post contains comment and auth user owner of comment!");
    }

    @Test
    public void test_isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin_False() {
        assertFalse(authCommentService.isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin(1L, 2L, 1L, "garry.potter"),
                "Here user does not owner of post, so here must be false.");
        assertFalse(authCommentService.isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin(2L, 1L, 1L, "garry.potter"),
                "Here post does not users, so here must be false.");
        assertFalse(authCommentService.isUsersSameAndOwnerOfPostAndPostContainCommentWithoutAdmin(1L, 1L, 3L, "garry.potter"),
                "Comment is not users, so here must be false.");
    }

    @Test
    public void test_GetComment() {
        long commentID = 3L;
        Comment expected = commentService.readById(commentID);
        Comment actual = authCommentService.getComment(commentID);

        assertEquals(expected, actual,
                "Comments read by the same id, so they must be the sames!");
    }
}
