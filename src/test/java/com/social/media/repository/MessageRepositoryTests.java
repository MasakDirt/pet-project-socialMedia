package com.social.media.repository;

import com.social.media.model.entity.Message;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MessageRepositoryTests {
    private final MessageRepository messageRepository;
    private final MessengerRepository messengerRepository;

    @Autowired
    public MessageRepositoryTests(MessageRepository messageRepository, MessengerRepository messengerRepository) {
        this.messageRepository = messageRepository;
        this.messengerRepository = messengerRepository;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(messageRepository).isNotNull();
        AssertionsForClassTypes.assertThat(messengerRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindAllByMessengerId() {
        long messengerId = 5L;

        List<Message> messages = messageRepository.findAllByMessengerId(messengerId);

        assertAll(
                () -> assertFalse(messages.isEmpty(),
                        "Messages that reads by messenger id should not be empty(it can be if messenger new)!"),
                () -> assertTrue(messages.size() < messageRepository.findAll().size(),
                        "Messages size must be smaller than all messages in db!"),
                () -> assertTrue(messages.stream()
                        .allMatch(message -> message.getMessengerId() == messengerId),
                        "All messages that read by messengerId must contains a valid messenger id.")
        );
    }

    @Test
    public void test_Invalid_FindAllByMessengerId() {
        assertTrue(messageRepository.findAllByMessengerId(0L).isEmpty(),
                "We have no messenger with id 0, so list must be empty.");
    }
}
