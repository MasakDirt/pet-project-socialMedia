package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.Message;
import com.social.media.model.entity.Messenger;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MessageServiceTests {
    private final MessageService messageService;

    private Set<Message> messages;

    @Autowired
    public MessageServiceTests(MessageService messageService) {
        this.messageService = messageService;
    }

    @BeforeEach
    public void setUp() {
        messages = messageService.getAll();
    }

    @Test
    public void test_Injected_Component() {
        assertThat(messageService).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertTrue(messageService.getAll().size() > 0,
                "Method get all must give back Set with bigger size than 0!");
    }

    @Test
    public void test_Valid_Create() {
        String message = "new message";
        long messengerId = 3L;

        Message expected = new Message();
        expected.setMessage(message);
        expected.setMessengerId(messengerId);

        Message actual = messageService.create(messengerId, message);

        assertEquals(expected, actual,
                "Messages expected and actual must be equal!");
    }

    @Test
    public void test_Invalid_Create() {
        long messengerId = 2L;
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> messageService.create(0, "message"),
                        "Here must be EntityNotFoundException because we have not messenger with id 0!"),
                () -> assertThrows(InvalidTextException.class, () -> messageService.create(messengerId, ""),
                        "Here must be InvalidTextException because message cannot be 'blank'!"),
                () -> assertThrows(InvalidTextException.class, () -> messageService.create(messengerId, null),
                        "Here must be InvalidTextException because message cannot be 'null'!")
        );
    }

    @Test
    public void test_Valid_ReadById() {
        Message expected = messageService.create(4L, "expected");
        Message actual = messageService.readById(expected.getId());

        assertEquals(expected, actual,
                "After reading actual message, they must be equal!");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> messageService.readById("0"),
                "Here we must get EntityNotFoundException because we have not message with id 0!");
    }

    @Test
    public void test_Valid_Update() {
        long messengerId = 2L;

        Message old = messageService.create(messengerId, "message");
        String oldId = old.getId();
        String oldMessage = old.getMessage();
        LocalDateTime oldTimestamp = old.getTimestamp();
        long oldMessengerId = old.getMessengerId();

        Message actual = messageService.update(oldId, "updating...");

        assertAll(
                () -> assertEquals(oldId, actual.getId(),
                        "Id`s after updating must be equals!"),
                () -> assertEquals(oldMessengerId, actual.getMessengerId(),
                        "Id`s after updating must be equals!"),
                () -> assertEquals(oldTimestamp.toLocalDate(), actual.getTimestamp().toLocalDate(),
                        "Timestamp after updating must be equal!"),


                () -> assertNotEquals(oldMessage, actual.getMessage(),
                        "Messages after updating must not equals.")
        );
    }

    @Test
    public void test_Invalid_Update() {
        Message message = messageService.create(4L, "message");
        String messageId = message.getId();

        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> messageService.update("0", "message"),
                        "Here must be EntityNotFoundException because we have not message with id 0!"),
                () -> assertThrows(InvalidTextException.class, () -> messageService.update(messageId, "   "),
                        "Here must be InvalidTextException because message cannot be 'blank'!"),
                () -> assertThrows(InvalidTextException.class, () -> messageService.update(messageId, null),
                        "Here must be InvalidTextException because message cannot be 'null'!")
        );
    }

    @Test
    public void test_Valid_Delete() {
        Message message = messageService.create(1L, "message");
        String messageId = message.getId();

        messageService.delete(messageId);

        assertEquals(messages, messageService.getAll(),
                "After deleting messages size must be the same, because we create one, and after that delete!");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> messageService.delete("0"),
                "Here must be EntityNotFoundException because we have not message with id 0!");
    }

    @Test
    public void test_Valid_ReadAllByMessenger() {
        long messengerId = 6L;

        List<Message> actual = messageService.readAllByMessenger(messengerId);

        assertTrue(actual.size() > 0);
        assertTrue(actual.stream()
                .anyMatch(message -> message.getMessengerId() == messengerId),
                "This assert must be true, if all messages which read by messenger id has sames messengerIds.");

    }

    @Test
    public void test_Invalid_ReadAllByMessenger() {
        assertThrows(EntityNotFoundException.class, () -> messageService.readAllByMessenger(0L),
                "Here must be EntityNotFoundException because we have not messenger with id 0!");
    }
}
