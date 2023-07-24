package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.Comment;
import com.social.media.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    public Comment create(long ownerId, long postId, String comment) {
        checkValidComment(comment);

        Comment commentObj = new Comment();
        commentObj.setComment(comment);
        commentObj.setOwner(userService.readById(ownerId));
        commentObj.setPost(postService.readById(postId));

        return commentRepository.save(commentObj);
    }

    public Comment readById(long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment with id" + id + "not found"));
    }

    public Comment update(long id, String updatedComment) {
        checkValidComment(updatedComment);

        var oldComment = readById(id);
        oldComment.setComment(updatedComment);

        return commentRepository.save(oldComment);
    }

    public void delete(long id) {
        commentRepository.delete(readById(id));
    }

    public Set<Comment> getAll() {
        return new HashSet<>(commentRepository.findAll());
    }

    private void checkValidComment(String comment) throws InvalidTextException {
        if (comment == null || comment.trim().isEmpty()) {
            throw new InvalidTextException("Comment need to be filled");
        }
    }
}
