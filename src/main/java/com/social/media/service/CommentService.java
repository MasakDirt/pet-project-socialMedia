package com.social.media.service;

import com.social.media.model.entity.Comment;
import com.social.media.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment create() {
        Comment comment = new Comment();
        return commentRepository.save(comment);
    }
}
