package com.social.media.service;

import com.social.media.exception.ConnectionToMinIOFailed;
import com.social.media.exception.LastPhotoException;
import com.social.media.exception.PhotoGettingException;
import com.social.media.exception.PostCreationException;
import com.social.media.minio.MinioClientImpl;
import com.social.media.model.entity.Photo;
import com.social.media.repository.PhotoRepository;
import io.minio.errors.MinioException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@AllArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final MinioClientImpl minioClient;

    public Photo create(Photo photo) {
        if (photo != null) {
            return photoRepository.save(photo);
        }
        throw new IllegalArgumentException("Photo cannot be 'null'");
    }

    public Photo readById(long id) {
        var photo = photoRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Photo with id " + id + "not found."));

        getPhoto(photo);
        return photo;
    }

    public void delete(long postId, long id) {
        if (isLastPhoto(postId) < 2) {
            throw new LastPhotoException("In post must be at least one photo!");
        }
        photoRepository.delete(readById(id));
    }

    public Set<Photo> getAll() {
        return new HashSet<>(photoRepository.findAll());
    }

    public List<Photo> getAllByPost(long postId) {
        return photoRepository.findAllByPostId(postId);
    }

    private int isLastPhoto(long postId) {
        return getAllByPost(postId).size();
    }

    private void getPhoto(Photo photo) {
        try {
            minioClient.getPhoto(photo.getPost().getOwner().getUsername(), photo.getFile().getPath().replace("\\", "/"));
        } catch (MinioException minioException) {
            throw new ConnectionToMinIOFailed("Connection failed: " + minioException.getMessage());
        } catch (Exception exception) {
            throw new PhotoGettingException("While photo is getting exception was throwing: " + exception.getMessage());
        }
    }
}
