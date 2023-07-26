package com.social.media.minio;

import com.social.media.exception.BucketCreationException;
import com.social.media.model.entity.User;
import com.social.media.service.UserService;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MinioClientImplTests {
    private final MinioClientImpl minioClient;
    private final UserService userService;

    private List<Bucket> buckets;
    private final String bucketName = "bucket.for-tests";
    private final String photoPath = "photos/photoForTests.webp";

    @Autowired
    public MinioClientImplTests(MinioClientImpl minioClient, UserService userService) {
        this.minioClient = minioClient;
        this.userService = userService;
    }

    @BeforeEach
    public void setBuckets() {
        buckets = minioClient.getBuckets();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(minioClient).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(buckets).isNotNull();
    }

    @Test
    public void test_GetBuckets() {
        assertTrue(minioClient.getBuckets().size() > 0);
    }

    @Test
    public void test_Valid_MakeBucketWithUsername() throws ServerException, InsufficientDataException, InvalidResponseException, XmlParserException, InternalException {
        String bucketName = "new-bucket";
        minioClient.makeBucketWithUsername(bucketName);

        assertEquals(bucketName, minioClient.getBucketByName(bucketName).name(),
                "Buckets name should be equals");
        assertTrue(minioClient.isBucketExist(bucketName),
                "We create this bucket so we must get true.");
    }

    @Test
    public void test_Invalid_MakeBucketWithUsername() {
        assertThrows(BucketCreationException.class, () -> minioClient.makeBucketWithUsername("not valid name for"),
                "Here must be BucketCreationException because Min IO has not create a buckets with spaces.");
    }

    @Test
    public void test_Valid_IsBucketExist() {
        User owner = userService.readById(1L);

        assertTrue(minioClient.isBucketExist(owner.getUsername()),
                "We create buckets by usernames so, it must exist and must be true.");
    }

    @Test
    public void test_Invalid_NotExist_IsBucketExist() {
        assertFalse(minioClient.isBucketExist("bucket.is-not.exist"),
                "We should get false because bucket with this name does not exist.");
    }

    @Test
    public void test_Valid_GetBucketByName() {
        User owner = userService.readById(2L);

        assertEquals(owner.getUsername(), minioClient.getBucketByName(owner.getUsername()).name(),
                "Buckets created bu users username so it`s must be same.");
    }

    @Test
    public void test_Invalid_GetBucketByName() {
        assertThrows(EntityNotFoundException.class, () -> minioClient.getBucketByName(""),
                "Here must be EntityNotFoundException because we have not bucket whit that name.");
    }

    @Test
    public void test_Valid_PutPhoto() throws ServerException, InsufficientDataException, InvalidResponseException, XmlParserException, InternalException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException {

        minioClient.makeBucketWithUsername(bucketName);
        assertThat(minioClient.putPhoto(bucketName, photoPath)).isNotNull();
    }

    @Test
    public void test_Invalid_PutPhoto() {
        assertThrows(IllegalArgumentException.class, () -> minioClient.putPhoto("", photoPath),
                "Here we must get IllegalArgumentException because we have not bucket with empty name!");
        assertThrows(IllegalArgumentException.class, () -> minioClient.putPhoto(bucketName, ""),
                "Here we must get IllegalArgumentException because we have not in bucket file with empty name");
    }

    @Test
    public void test_Valid_GetPhoto() throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        User owner = userService.readById(3L);
        String photoFilePath = "photos/small_cat.jpg";
        Path path = Path.of("downloaded/" + photoFilePath);

        minioClient.getPhoto(owner.getUsername(), photoFilePath);

        assertTrue(Files.exists(path),
                "After getting the photo file must be created in path: downloaded/" + photoFilePath);
    }

    @Test
    public void test_Invalid_GetPhoto() {
        assertThrows(IllegalArgumentException.class, () -> minioClient.getPhoto("", photoPath),
                "Here we must get IllegalArgumentException because we have not bucket with empty name!");
    }

    @Test
    public void test_Valid_IsDirectoryExist() {
        assertTrue(minioClient.isDirectoryExist(Path.of(".mvn/wrapper/maven-wrapper.properties")),
                "Here must be true because that file already exist in project!");
    }

    @Test
    public void test_Invalid_IsDirectoryExist() {
        assertFalse(minioClient.isDirectoryExist(Path.of("directory/that/never/be/created")),
                "Here must be false because we have not this directory in project");
    }
}
