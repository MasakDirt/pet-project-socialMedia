package com.social.media.service;

import com.social.media.exception.ConnectionToMinIOFailed;
import com.social.media.exception.InvalidTextException;
import com.social.media.exception.PhotoDoesNotExist;
import com.social.media.exception.PostCreationException;
import com.social.media.minio.MinioClientImpl;
import com.social.media.model.entity.Photo;
import com.social.media.model.entity.Post;
import com.social.media.repository.PostRepository;
import io.minio.errors.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PhotoService photoService;
    private final MinioClientImpl minioClient;

    public Post create(long ownerId, String description, List<String> filePaths) {
        checkDescriptionForNull(description);

        checkPathsForNull(filePaths);

        var post = saveToDB(ownerId, description);
        var photos = getPhotos(filePaths, post);
        post.setPhotos(photos);

        return postRepository.save(post);
    }

    public Post readById(long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Post with id " + id + " not found!"));
    }

    public Post readByOwnerIdAndId(long ownerId , long id){
        return postRepository.findByOwnerIdAndId(ownerId, id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User post with user id: %d, and post id: %d, not found", ownerId, id))
        );
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

    public List<Post> getUserPosts(long ownerId) {
        return postRepository.findAllByOwnerId(ownerId);
    }

    private Set<Photo> getPhotos(List<String> filePaths, Post post) {
        var photos = createNewPhotos(new HashSet<>(), filePaths, post);
        makeBucketAndPutPhotoToMinIO(post.getOwner().getUsername(), filePaths);

        return photos;
    }

    private Set<Photo> createNewPhotos(Set<Photo> photos, List<String> filePaths, Post post) {
        filePaths.forEach(path -> {
            var photo = new Photo();
            photo.setPost(post);
            photo.setFile(new File(path));
            photos.add(photoService.create(photo));
        });
        return photos;
    }

    private Post saveToDB(long ownerId, String description) {
        var owner = userService.readById(ownerId);

        var post = new Post();
        post.setDescription(description);
        post.setOwner(owner);
        return postRepository.save(post);
    }

    private void makeBucketAndPutPhotoToMinIO(String username, List<String> filePaths) {
        makeBucketIfIsNotExist(username);

        putPhotos(username, filePaths);
    }

    private void makeBucketIfIsNotExist(String username) {
        if (!minioClient.isBucketExist(username)) {
            try {
                minioClient.makeBucketWithUsername(username);
            } catch (MinioException minioException) {
                throw new ConnectionToMinIOFailed("Connection failed: " + minioException.getMessage());
            } catch (Exception exception) {
                throw new PostCreationException("While post is creating exception was throwing: " + exception.getMessage());
            }
        }
    }

    private void putPhotos(String username, List<String> photosPaths) {
        photosPaths.forEach(photo -> {
                    try {
                        minioClient.putPhoto(username, photo);
                    } catch (MinioException minioException) {
                        throw new ConnectionToMinIOFailed("Connection failed: " + minioException.getMessage());
                    } catch (Exception exception) {
                        throw new PostCreationException("While post is creating exception was throwing: " + exception.getMessage());
                    }
                }
        );
    }

    private void checkDescriptionForNull(String description) {
        if (description == null) {
            throw new InvalidTextException("Description can be blank, but not null!");
        }
    }

    private void checkPathsForNull(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            throw new PhotoDoesNotExist("You need to paste at least one photo");
        }

        paths.forEach(path -> {
            if (path.trim().isEmpty()){
                throw new InvalidTextException("You need to write valid photo path");
            }
        });
    }
}
