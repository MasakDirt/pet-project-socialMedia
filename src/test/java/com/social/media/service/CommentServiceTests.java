package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.Comment;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class CommentServiceTests {
    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;

    private Set<Comment> comments;

    @Autowired
    public CommentServiceTests(CommentService commentService, UserService userService, PostService postService) {
        this.commentService = commentService;
        this.userService = userService;
        this.postService = postService;
    }

    @Test
    public void test_Injected_Component() {
        assertThat(commentService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(postService).isNotNull();
    }

    @BeforeEach
    public void setUp() {
        comments = commentService.getAll();
    }

    @Test
    public void test_GetAll() {
        assertTrue(0 < commentService.getAll().size(),
                "This condition must be true, because we have much bigger comments than 0.");
        assertEquals(comments, commentService.getAll(),
                "This condition must be true.");
    }

    @Test
    public void test_Valid_Create_ByOwnerId() {
        long ownerId = 1L;
        long postId = 4L;
        String comment = "Comment";

        Comment expected = new Comment();
        expected.setComment(comment);
        expected.setPost(postService.readById(postId));
        expected.setOwner(userService.readById(ownerId));

        Comment actual = commentService.create(ownerId, postId, comment);
        expected.setId(actual.getId());

        assertTrue(comments.size() < commentService.getAll().size(),
                "After creating new comment commentService.getAll size must be bigger than comments size before");
        assertEquals(expected, actual,
                "Expected and actual comments must be equal, because we create it with common objects");
    }

    @Test
    public void test_Invalid_Create_ByOwnerId() {
        long ownerId = 2L;
        long postId = 4L;
        String comment = "Comment";

        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> commentService.create(1000000L, postId, comment),
                        "Here must be EntityNotFoundException because we have not in db user with id 1000000"),

                () -> assertThrows(EntityNotFoundException.class, () -> commentService.create(ownerId, 100000L, comment),
                        "Here must be EntityNotFoundException because we have not in db post with id 100000"),

                () -> assertThrows(InvalidTextException.class, () -> commentService.create(ownerId, postId, null),
                        "Here must be InvalidTextException because we cannot transmit 'null' in parameter comment in method create!"),

                () -> assertThrows(InvalidTextException.class, () -> commentService.create(ownerId, postId, ""),
                        "Here must be InvalidTextException because we cannot transmit blank comment in it`s parameter in method create!")
        );
    }

    @Test
    public void test_Valid_Create_ByOwnerUsername() {
        String ownerUsername = "oil";
        long postId = 4L;
        String comment = "Comment";

        Comment expected = new Comment();
        expected.setComment(comment);
        expected.setPost(postService.readById(postId));
        expected.setOwner(userService.readByUsername(ownerUsername));

        Comment actual = commentService.create(ownerUsername, postId, comment);
        expected.setId(actual.getId());

        assertTrue(comments.size() < commentService.getAll().size(),
                "After creating new comment commentService.getAll size must be bigger than comments size before");
        assertEquals(expected, actual,
                "Expected and actual comments must be equal, because we create it with common objects");
    }

    @Test
    public void test_Invalid_Create_ByOwnerUsername() {
        String ownerUsername = "garry.potter";
        long postId = 4L;
        String comment = "Comment";

        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> commentService.create("invalid.username", postId, comment),
                        "Here must be EntityNotFoundException because we have not in db user with this username"),

                () -> assertThrows(EntityNotFoundException.class, () -> commentService.create(ownerUsername, 100000L, comment),
                        "Here must be EntityNotFoundException because we have not in db post with id 100000"),

                () -> assertThrows(InvalidTextException.class, () -> commentService.create(ownerUsername, postId, null),
                        "Here must be InvalidTextException because we cannot transmit 'null' in parameter comment in method create!"),

                () -> assertThrows(InvalidTextException.class, () -> commentService.create(ownerUsername, postId, ""),
                        "Here must be InvalidTextException because we cannot transmit blank comment in it`s parameter in method create!")
        );
    }

    @Test
    public void test_Valid_ReadById() {
        Comment expected = commentService.create(1L, 3L, "Hello");
        Comment actual = commentService.readById(expected.getId());

        assertEquals(expected, actual,
                "After reading by id expected and actual objects must be Equal.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> commentService.readById(0L),
                "Here must be EntityNotFoundException because we have not comment with id 0!");
    }

    @Test
    public void test_Valid_Update() {
        long id = 3L;
        Comment oldComment = commentService.readById(id);

        long oldId = oldComment.getId();
        Post oldPost = oldComment.getPost();
        User oldOwner = oldComment.getOwner();
        String oldCommentText = oldComment.getComment();

        Comment newComment = commentService.update(id, "comment for update");

        assertAll(
                () -> assertEquals(oldId, newComment.getId(),
                        "After updating comments id must be equal."),
                () -> assertEquals(oldPost, newComment.getPost(),
                        "After updating posts of old and new comments must be equal"),
                () -> assertEquals(oldOwner, newComment.getOwner(),
                        "After updating owners of old and new comments must be equal"),

                () -> assertNotEquals(oldCommentText, newComment.getComment(),
                        "After updating comments of old and new comments must not be equal")
        );
    }

    @Test
    public void test_Invalid_Update() {
        long id = 4L;
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> commentService.update(0L, "hello"),
                        "Here must be EntityNotFoundException because we have not comment with id 0."),

                () -> assertThrows(InvalidTextException.class, () -> commentService.update(id, null),
                        "Here must be InvalidTextException because we can not pass the 'null' comment!"),

                () -> assertThrows(InvalidTextException.class, () -> commentService.update(id, "   "),
                        "Here must be InvalidTextException because we can not pass the 'blank' comment even though we have a spaces!")
        );
    }

    @Test
    public void test_Valid_Delete() {
        long id = 3L;

        commentService.delete(id);
        assertTrue(comments.size() > commentService.getAll().size(),
                "This condition must be true, because after deleting our commentService.getAll().size() must be less by 1 comment!");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> commentService.delete(0),
                "Here must be EntityNotFoundException because we have not comment with id 0.");
    }

    @Test
    public void test_Valid_GetAllByPostId() {
        long postId = 4L;
        Post post = postService.readById(postId);

        List<Comment> actualComments = commentService.getAllByPostId(postId);

        assertAll(
                () -> assertTrue(actualComments.size() > 0,
                        "Comments under the post must be bigger than 0."),
                () -> assertTrue(actualComments.size() < comments.size(),
                        "All comments must be bigger than comments under the post!"),
                () -> assertEquals(post.getComments().size(), actualComments.size(),
                        "Post comments and service comments reading under the post must be equal.")
        );
    }

    @Test
    public void test_Invalid_GetAllByPostId() {
        assertTrue(commentService.getAllByPostId(0L).isEmpty(),
                "When we reading not existing post, here must be empty list");
    }

    @Test
    public void test_Valid_GetAllByOwnerId() {
        long ownerId = 1L;
        User owner = userService.readById(ownerId);

        List<Comment> actualComments = commentService.getAllByOwnerId(ownerId);

        assertAll(
                () -> assertFalse(actualComments.isEmpty(),
                        "Comments under the post should not be empty!"),
                () -> assertTrue(actualComments.size() < comments.size(),
                        "All comments must be bigger than user comments!"),
                () -> assertEquals(owner.getMyComments().size(), actualComments.size(),
                        "User comments and service comments reading must be equal.")
        );
    }

    @Test
    public void test_Invalid_GetAllByOwnerId() {
        assertTrue(commentService.getAllByOwnerId(0L).isEmpty(),
                "When we reading not existing user, here must be empty list");
    }
}
