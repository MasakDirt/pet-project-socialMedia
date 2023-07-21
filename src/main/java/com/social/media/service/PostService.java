package com.social.media.service;

import com.social.media.minio.MinioClientImpl;
import com.social.media.model.entity.Post;
import com.social.media.repository.PostRepository;
import io.minio.errors.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public Post create(
            long ownerId,
            @NotNull String description,
            @NotNull @NotBlank(message = "Photo file path cannot be blank") String photoFile
    ) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        var owner = userService.readById(ownerId);
        makeBucketAndPutPhotoToMinIO(owner.getUsername(), photoFile);

        var post = new Post();
        post.setPhoto(new File(photoFile));
        post.setDescription(description);
        post.setOwner(owner);
        return postRepository.save(post);
    }

    public Post readById(long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Post with id" + id + "not found!"));
    }

    public Post update(@NotNull Post updatedPost) {
        readById(updatedPost.getId());
        return postRepository.save(updatedPost);
    }

    public void delete(long id) {
        postRepository.delete(readById(id));
    }

    private void makeBucketAndPutPhotoToMinIO(String username, String file) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        MinioClientImpl minioClient = new MinioClientImpl();
        if (!minioClient.isBucketExist(username)) {
            minioClient.makeBucketWithUsername(username);
        }
        minioClient.putPhoto(username, file);
    }
}
