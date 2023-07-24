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
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MessageServiceTests {
    private final MessageService messageService;
    private final MessengerService messengerService;

    private Set<Message> messages;

    @Autowired
    public MessageServiceTests(MessageService messageService, MessengerService messengerService) {
        this.messageService = messageService;
        this.messengerService = messengerService;
    }

    @BeforeEach
    public void setUp() {
        messages = messageService.getAll();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(messageService).isNotNull();
        assertThat(messengerService).isNotNull();
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
        expected.setMessenger(messengerService.readById(messengerId));

        Message actual = messageService.create(messengerId, message);
        expected.setId(actual.getId());

        assertTrue(messages.size() < messageService.getAll().size(),
                "Size of messages must be bigger after creating one.");

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
        assertThrows(EntityNotFoundException.class, () -> messageService.readById(0L),
                "Here we must get EntityNotFoundException because we have not message with id 0!");
    }

    @Test
    public void test_Valid_Update() {
        long messageId = 5L;

        Message old = messageService.readById(messageId);
        long oldId = old.getId();
        String oldMessage = old.getMessage();
        LocalDateTime oldTimestamp = old.getTimestamp();
        Messenger oldMessenger = old.getMessenger();

        Message actual = messageService.update(messageId, "updating...");

        assertAll(
                () -> assertEquals(oldId, actual.getId(),
                        "Id`s after updating must be equals!"),
                () -> assertEquals(oldMessenger, actual.getMessenger(),
                        "Messengers after updating must be equal!"),
                () -> assertEquals(oldTimestamp, actual.getTimestamp(),
                        "Timestamp after updating must be equal!"),


                () -> assertNotEquals(oldMessage, actual.getMessage(),
                        "Messages after updating must not equals ")
        );
    }

    @Test
    public void test_Invalid_Update() {
        long messageId = 8L;
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> messageService.update(0, "message"),
                        "Here must be EntityNotFoundException because we have not message with id 0!"),
                () -> assertThrows(InvalidTextException.class, () -> messageService.update(messageId, "   "),
                        "Here must be InvalidTextException because message cannot be 'blank'!"),
                () -> assertThrows(InvalidTextException.class, () -> messageService.update(messageId, null),
                        "Here must be InvalidTextException because message cannot be 'null'!")
        );
    }

    @Test
    public void test_Valid_Delete() {
        messageService.delete(1L);

        assertTrue(messages.size() > messageService.getAll().size(),
                "After deleting messages size must be bigger!");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> messageService.delete(0L),
                "Here must be EntityNotFoundException because we have not message with id 0!");
    }

    @Test
    public void test_Valid_ReadAllByMessenger() {
        long messengerId = 6L;

        List<Message> expected = messengerService.readById(messengerId).getMessages();
        List<Message> actual = messageService.readAllByMessenger(messengerId);

        for (int i = 0; i < expected.size(); i++) {
            assertThat(expected.get(i)).isEqualTo(actual.get(i)).describedAs("");
        }
    }

    @Test
    public void test_Invalid_ReadAllByMessenger() {
        assertThrows(EntityNotFoundException.class, () -> messageService.readAllByMessenger(0L),
                "Here must be EntityNotFoundException because we have not messenger with id 0!");
    }
}
