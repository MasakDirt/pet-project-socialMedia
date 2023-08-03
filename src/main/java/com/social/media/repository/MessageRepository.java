package com.social.media.repository;

import com.social.media.model.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByMessengerId(long messengerId);
}
