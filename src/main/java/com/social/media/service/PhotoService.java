package com.social.media.service;

import com.social.media.exception.LastPhotoException;
import com.social.media.model.entity.Photo;
import com.social.media.repository.PhotoRepository;
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

    public Photo create(Photo photo) {
        if (photo != null) {
            return photoRepository.save(photo);
        }
        throw new IllegalArgumentException("Photo cannot be 'null'");
    }

    public Photo readById(long id) {
        return photoRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Photo with id " + id + "not found."));
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
}
