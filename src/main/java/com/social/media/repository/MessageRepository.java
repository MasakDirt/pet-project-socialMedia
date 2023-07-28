package com.social.media.repository;

import com.social.media.model.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;

@EnableMongoRepositories
public interface MessageRepository extends MongoRepository<Message, Long> {
    List<Message> findAllByMessengerId(@Param("messengerId") long messengerId);
}
