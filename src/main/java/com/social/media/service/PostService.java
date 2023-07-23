package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.minio.MinioClientImpl;
import com.social.media.model.entity.Post;
import com.social.media.repository.PostRepository;
import io.minio.errors.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public Post create(long ownerId, String description, String photoFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        checkDescriptionForNull(description);

        if (photoFile == null || photoFile.trim().isEmpty()) {
            throw new InvalidTextException("You need to paste a photo");
        }

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
                new EntityNotFoundException("Post with id " + id + " not found!"));
    }

    public Post update(long postId, String updatedDescription) {
        checkDescriptionForNull(updatedDescription);

        var oldPost = readById(postId);
        oldPost.setDescription(updatedDescription);

        return postRepository.save(oldPost);
    }

    public void delete(long id) {
        postRepository.delete(readById(id));
    }

    public Set<Post> getAll() {
        return new HashSet<>(postRepository.findAll());
    }

    private void makeBucketAndPutPhotoToMinIO(String username, String file) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        MinioClientImpl minioClient = new MinioClientImpl();
        if (!minioClient.isBucketExist(username)) {
            minioClient.makeBucketWithUsername(username);
        }
        minioClient.putPhoto(username, file);
    }

    private void checkDescriptionForNull(String description) {
        if (description == null){
            throw new InvalidTextException("Description can be blank, but not null!");
        }
    }
}
