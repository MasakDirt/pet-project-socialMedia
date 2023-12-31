package com.social.media.repository;

import com.social.media.model.entity.Like;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByOwnerAndPost(User owner, Post post);

    List<Like> findAllByPostId(long postId);

    List<Like> findAllByOwnerId(long ownerId);
}
