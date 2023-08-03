package com.social.media.repository;

import com.social.media.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOwnerId(long ownerId);

    Optional<Post> findByOwnerIdAndId(long ownerId, long id);
}
