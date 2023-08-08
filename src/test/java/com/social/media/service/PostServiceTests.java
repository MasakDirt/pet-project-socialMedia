package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.exception.PhotoDoesNotExist;
import com.social.media.minio.MinioClientImpl;
import com.social.media.model.entity.Photo;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import io.minio.errors.*;
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
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class PostServiceTests {
    private final PostService postService;
    private final UserService userService;

    private List<Post> posts;

    @Autowired
    public PostServiceTests(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @BeforeEach
    public void setPosts() {
        posts = postService.getAll();
    }

    @Test
    public void test_Injected_Component() {
        assertThat(postService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(posts).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertTrue(postService.getAll().size() > 0,
                "After getting all post they size must be bigger than 0.");
        assertEquals(posts, postService.getAll(),
                "Before and now getting posts must be th same!");
    }

    @Test
    public void test_Valid_Create() {
        long ownerId = 2L;
        String description = "";
        String file = "photos/catshark.webp";

        Photo photo = new Photo();
        photo.setFile(new File(file));

        Post expected = new Post();
        expected.setOwner(userService.readById(ownerId));
        expected.setDescription(description);
        expected.setPhotos(List.of(photo));
        photo.setPost(expected);

        Post actual = postService.create(ownerId, description, List.of(file));
        expected.setId(actual.getId());

        assertTrue(posts.size() < postService.getAll().size(),
                "After creating new post getALl method must contains one more post than posts before");
        assertEquals(expected, actual,
                "After creating they must be equals.");
    }

    @Test
    public void test_Invalid_Create() {
        long ownerId = 2L;
        String photoFile = "photos/mcLaren.jpg";
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> postService.create(0L, "", List.of(photoFile)),
                        "Here must be EntityNotFoundException because we have not user with id 0."),

                () -> assertThrows(InvalidTextException.class, () -> postService.create(ownerId, null, List.of(photoFile)),
                        "Here must be InvalidTextException because description cannot be null."),

                () -> assertThrows(InvalidTextException.class, () -> postService.create(ownerId, "", List.of("   ")),
                        "Here must be InvalidTextException because photoFiles cannot be 'blank'."),

                () -> assertThrows(PhotoDoesNotExist.class, () -> postService.create(ownerId, "", new ArrayList<>()),
                        "Here must be PhotoDoesNotExist because list of photos paths cannot be empty."),

                () -> assertThrows(PhotoDoesNotExist.class, () -> postService.create(ownerId, "", null),
                        "Here must be PhotoDoesNotExist because list of photos paths cannot be null.")
        );
    }

    @Test
    public void test_Putting_And_Getting_Photo_To_And_From_MinIO() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        User owner = userService.readById(2L);
        String photoFile = "photos/girl.webp";

        Post post = postService.create(owner.getId(), "description", List.of(photoFile));

        MinioClientImpl minioClient = new MinioClientImpl();
        minioClient.getPhoto(owner.getUsername(), photoFile);

        assertEquals(post.getPhotos().iterator().next().getFile().getName(), new File("downloaded/" + photoFile).getName(),
                "Files names must be equal, but with directories they don`t equal.");
    }

    @Test
    public void test_Valid_ReadById() {

        Post expected = postService.create(1L, "", List.of("photos/nature-photography.webp"));
        Post actual = postService.readById(expected.getId());

        assertEquals(expected, actual,
                "They must be equal after reading by id!");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> postService.readById(0L),
                "Here must be EntityNotFoundException because we have not post with id 0.");
    }

    @Test
    public void test_Valid_ReadByOwnerIdAndId() {
        User owner = userService.readById(3L);
        Post expected = owner.getMyPosts().stream().findFirst().orElseThrow(EntityNotFoundException::new);

        Post actual = postService.readByOwnerIdAndId(owner.getId(), expected.getId());

        assertEquals(expected, actual,
                "We find in user list of posts one post, and get it, than we read post by it`s id and posts objects must be sames!");
    }

    @Test
    public void test_Invalid_ReadByOwnerIdAndId() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> postService.readByOwnerIdAndId(1L, 0L),
                        "We have no post with id 0, so here must be EntityNotFoundException"),

                () -> assertThrows(EntityNotFoundException.class, () -> postService.readByOwnerIdAndId(0L, 2L),
                        "We have no user with id 0, so here must be EntityNotFoundException")
        );
    }

    @Test
    public void test_Valid_Update() {
        long postId = 4L;
        String newDescription = "new description";

        Post post = postService.readById(postId);
        long oldId = post.getId();
        String oldDescription = post.getDescription();
        List<Photo> oldPhotos = post.getPhotos();
        User oldOwner = post.getOwner();
        LocalDateTime oldTime = post.getTimestamp();

        Post actual = postService.update(postId, newDescription);

        assertAll(
                () -> assertEquals(oldId, actual.getId(),
                        "Id`s after updating must not change!"),

                () -> assertEquals(oldPhotos, actual.getPhotos(),
                        "Photo`s after updating must not change!"),

                () -> assertEquals(oldOwner, actual.getOwner(),
                        "Owner`s after updating must not change!"),

                () -> assertEquals(oldTime, actual.getTimestamp(),
                        "Timestamp`s after updating must not change!"),

                () -> assertNotEquals(oldDescription, actual.getDescription(),
                        "Description`s after updating must change!"),

                () -> assertEquals(newDescription, actual.getDescription(),
                        "Description`s must be the same!")
        );
    }

    @Test
    public void test_Invalid_Update() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> postService.update(0L, ""),
                        "Here must be EntityNotFoundException because we have not post with id 0!"),
                () -> assertThrows(InvalidTextException.class, () -> postService.update(2L, null),
                        "Here must be InvalidTextException because description cannot be null.")
        );
    }

    @Test
    public void test_Valid_Delete() {
        postService.delete(1L);

        assertTrue(posts.size() > postService.getAll().size(),
                "After deleting post service collection must be smaller than before deleting");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> postService.delete(0L),
                "Here must be EntityNotFoundException because we have not post with id 0!");
    }

    @Test
    public void test_Valid_GetUserPost() {
        long ownerId = 3L;
        User owner = userService.readById(ownerId);

        List<Post> actualPosts = postService.getUserPosts(ownerId);

        assertAll(
                () -> assertFalse(actualPosts.isEmpty(),
                        "Owner posts list should contains one post!"),
                () -> assertTrue(actualPosts.size() < posts.size(),
                        "All posts size must be bigger than user posts."),
                () -> assertEquals(actualPosts.size(), owner.getMyPosts().size(),
                        "User posts must be the same with all posts which reads by owner id.")
        );
    }

    @Test
    public void test_Invalid_GetUserPost() {
        assertTrue(postService.getUserPosts(0L).isEmpty(),
                "We have no user with id 0, so here must be empty list.");
    }
}
