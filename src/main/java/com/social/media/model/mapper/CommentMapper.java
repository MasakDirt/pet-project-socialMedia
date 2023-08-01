package com.social.media.model.mapper;

import com.social.media.model.dto.comment.CommentResponseForOwner;
import com.social.media.model.dto.comment.CommentResponseForPost;
import com.social.media.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "ownerUsername", expression = "java(comment.getOwner().getUsername())")
    CommentResponseForPost createCommentResponseForPostFromComment(Comment comment);

    @Mapping(target = "postId", expression = "java(comment.getPost().getId())")
    @Mapping(target = "photos", expression = "java(comment.getPost().getPhotos())")
    @Mapping(target = "description", expression = "java(comment.getPost().getDescription())")
    CommentResponseForOwner createCommentResponseForOwnerFromComment(Comment comment);
}
