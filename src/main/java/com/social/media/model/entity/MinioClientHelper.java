package com.social.media.model.entity;

import io.minio.*;
import io.minio.errors.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioClientHelper {
    private static final MinioClient minioClient = getMinioClient();

    public void makeBucketWithUsername(String username) throws ErrorResponseException, ServerException,
            InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        minioClient.makeBucket(
                MakeBucketArgs
                        .builder()
                        .bucket(username)
                        .build()
        );
    }

    public ObjectWriteResponse putPhoto(String username, String fileName) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs
                .builder()
                .bucket(username)
                .object(fileName)
                .filename("/photos/" + fileName)
                .build();

        return minioClient.uploadObject(uploadObjectArgs);
    }

    public void getPhoto(String username, String fileName) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        DownloadObjectArgs dArgs = DownloadObjectArgs.builder()
                .bucket(username)
                .object(fileName)
                .filename("/photos/" + fileName)
                .build();

        minioClient.downloadObject(dArgs);
    }

    private static MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint("http://127.0.0.1:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    private InputStream getInputStream(String username, String fileName) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(username)
                        .object(fileName)
                        .build()
        );
    }
}
