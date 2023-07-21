package com.social.media.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MinioClientImpl {
    private static final MinioClient minioClient = getMinioClient();

    public void makeBucketWithUsername(String username) throws ServerException,
            InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try {
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(username)
                            .build()
            );
        } catch (ErrorResponseException responseException) {
            log.info("Your previous request to create the named bucket succeeded and you already own it.");
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
        DownloadObjectArgs dArgs = DownloadObjectArgs.builder()
                .bucket(username)
                .object(fileName)
                .filename(fileName)
                .build();

        minioClient.downloadObject(dArgs);
    }

    public boolean isBucketExist(String username) {
        return !getBuckets().isEmpty() && getBuckets()
                .stream()
                .anyMatch(bucket -> bucket.name().equals(username));
    }

    private List<Bucket> getBuckets() {
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
}
