package com.social.media.model.mapper;

import com.social.media.model.dto.photo.PhotoResponse;
import com.social.media.model.entity.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
    @Mapping(target = "postId", expression = "java(photo.getPost().getId())")
    @Mapping(target = "timestamp", expression = "java(photo.getPost().getTimestamp())")
    @Mapping(target = "description", expression = "java(photo.getPost().getDescription())")
    PhotoResponse createPhotoResponseFromPhoto(Photo photo);
}
