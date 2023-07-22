package com.social.media.repository;

import com.social.media.model.entity.Message;
import com.social.media.model.entity.Messenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
//    @Query(value = "select m from Messenger m join fetch m.messages where m = :messenger")
    List<Message> findAllByMessenger(@Param("messenger") Messenger messenger);
}
