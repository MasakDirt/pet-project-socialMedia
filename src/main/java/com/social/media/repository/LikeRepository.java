package com.social.media.repository;

import com.social.media.model.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
}
