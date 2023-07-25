package com.social.media.minio;

import com.social.media.exception.BucketCreationException;
import com.social.media.exception.ConnectionToMinIOFailed;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MinioClientImpl {
    private static final MinioClient minioClient = getMinioClient();

    public void makeBucketWithUsername(String username) throws ServerException,
            InsufficientDataException, InvalidResponseException, XmlParserException, InternalException {
        try {
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(username)
                            .build()
            );
        } catch (ErrorResponseException responseException) {
            log.warn("Your previous request to create the named bucket succeeded and you already own it.");
        } catch (MinioException minioException) {
            throw new ConnectionToMinIOFailed("Connection failed: " + minioException.getMessage());
        } catch (Exception exception) {
            throw new BucketCreationException("While post is creating exception was throwing: " + exception.getMessage());
        }
    }

    public ObjectWriteResponse putPhoto(String username, String fileName) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs
                .builder()
                .bucket(username)
                .object(fileName)
                .filename(fileName)
                .build();

        return minioClient.uploadObject(uploadObjectArgs);
    }

    public void getPhoto(String username, String fileName) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Path pathForFolder = Paths.get("downloaded/photos");
        Path pathForPhoto = Paths.get("downloaded/" + fileName);

        creatingFolderForGettingPhoto(pathForFolder);

        if (!isDirectoryExist(pathForPhoto)){
            downloadObject(username, fileName, pathForPhoto);
        }
    }

    public boolean isBucketExist(String username) {
        return !getBuckets().isEmpty() && getBuckets()
                .stream()
                .anyMatch(bucket -> bucket.name().equals(username));
    }

    public Bucket getBucketByName(String username) {
       return getBuckets()
                .stream()
                .filter(bucket -> bucket.name().equals(username))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Bucket with name " + username + " not found"));
    }

    public boolean isDirectoryExist(Path path) {
        return Files.exists(path);
    }

    public List<Bucket> getBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (MinioException minioException) {
            log.error("Connection failed: {}", minioException.getMessage());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return new ArrayList<>();
    }

    private static MinioClient getMinioClient() {
        return io.minio.MinioClient.builder()
                .endpoint("https://play.min.io")
                .credentials("Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")
                .build();
    }

    private void creatingFolderForGettingPhoto(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("New folder with path: {}, is created", path);
        }
    }

    private void downloadObject(String username, String fileName, Path destinationPath) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        DownloadObjectArgs dArgs = DownloadObjectArgs.builder()
                .bucket(username)
                .object(fileName)
                .filename(destinationPath.toString())
                .build();

        minioClient.downloadObject(dArgs);
    }
}
