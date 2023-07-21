package com.social.media.service;

import com.social.media.model.entity.Comment;
import com.social.media.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    public Comment create(long ownerId, long postId, Comment comment) {
        comment.setOwner(userService.readById(ownerId));
        comment.setPost(postService.readById(postId));

        return commentRepository.save(comment);
    }

    public Comment readById(long id){
        return commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment with id" + id + "not found"));
    }

    public Comment update(Comment updatedComment) {
        readById(updatedComment.getId());
       return commentRepository.save(updatedComment);
    }

    public void delete(long id){
        commentRepository.delete(readById(id));
    }
}
