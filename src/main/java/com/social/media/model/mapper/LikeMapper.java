package com.social.media.model.mapper;

import com.social.media.model.dto.like.LikeResponseForOwner;
import com.social.media.model.dto.like.LikeResponseForPosts;
import com.social.media.model.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(target = "ownerUsername", expression = "java(like.getOwner().getUsername())")
    LikeResponseForPosts createLikeResponseForPostsFromLike(Like like);

    @Mapping(target = "postId", expression = "java(like.getPost().getId())")
    @Mapping(target = "photos", expression = "java(like.getPost().getPhotos())")
    @Mapping(target = "description", expression = "java(like.getPost().getDescription())")
    LikeResponseForOwner createLikeResponseForOwnerFromLike(Like like);
}
