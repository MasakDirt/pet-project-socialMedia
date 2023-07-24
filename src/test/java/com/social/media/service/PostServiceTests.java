package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.minio.MinioClientImpl;
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
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class PostServiceTests {
    private final PostService postService;
    private final UserService userService;

    private Set<Post> posts;

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
    public void test_Valid_Create() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        long ownerId = 2L;
        String description = "";
        String file = "photos/catshark.webp";

        Post expected = new Post();
        expected.setOwner(userService.readById(ownerId));
        expected.setDescription(description);
        expected.setPhoto(new File(file));

        Post actual = postService.create(ownerId, description, file);
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
                () -> assertThrows(EntityNotFoundException.class, () -> postService.create(0L, "", photoFile),
                        "Here must be EntityNotFoundException because we have not user with id 0."),

                () -> assertThrows(InvalidTextException.class, () -> postService.create(ownerId, null, photoFile),
                        "Here must be InvalidTextException because description cannot be null."),

                () -> assertThrows(InvalidTextException.class, () -> postService.create(ownerId, "", ""),
                        "Here must be InvalidTextException because photoFile cannot be 'blank'."),

                () -> assertThrows(InvalidTextException.class, () -> postService.create(ownerId, "", null),
                        "Here must be InvalidTextException because photoFile cannot be null.")
        );
    }

    @Test
    public void test_Putting_And_Getting_Photo_To_And_From_MinIO() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        User owner = userService.readById(2L);
        String photoFile = "photos/girl.webp";

        Post post = postService.create(owner.getId(), "description", photoFile);

        MinioClientImpl minioClient = new MinioClientImpl();
        minioClient.getPhoto(owner.getUsername(), photoFile);

        assertEquals(post.getPhoto().getName(), new File("downloaded/" + photoFile).getName(),
                "Files names must be equal, but with directories they don`t equal.");
    }

    @Test
    public void test_Valid_ReadById() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Post expected = postService.create(1L, "", "photos/nature-photography.webp");
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
    public void test_Valid_Update() {
        long postId = 4L;
        String newDescription = "new description";

        Post post = postService.readById(postId);
        long oldId = post.getId();
        String oldDescription = post.getDescription();
        File oldPhoto = post.getPhoto();
        User oldOwner = post.getOwner();
        LocalDateTime oldTime = post.getTimestamp();

        Post actual = postService.update(postId, newDescription);

        assertAll(
                () -> assertEquals(oldId, actual.getId(),
                        "Id`s after updating must not change!"),

                () -> assertEquals(oldPhoto, actual.getPhoto(),
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
}
