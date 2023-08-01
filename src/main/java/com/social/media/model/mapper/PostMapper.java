package com.social.media.model.mapper;

import com.social.media.model.dto.post.PostResponse;
import com.social.media.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "likes", expression = "java(post.getLikes().size())")
    @Mapping(target = "comments", expression = "java(post.getComments().size())")
    @Mapping(target = "ownerUsername", expression = "java(post.getOwner().getUsername())")
    PostResponse createPostResponseFromPost(Post post);
}
