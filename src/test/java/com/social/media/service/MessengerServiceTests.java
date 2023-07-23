package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.exception.SameUsersException;
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

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MessengerServiceTests {
    private final MessengerService messengerService;
    private final UserService userService;

    private Set<Messenger> messengers;

    @Autowired
    public MessengerServiceTests(MessengerService messengerService, UserService userService) {
        this.messengerService = messengerService;
        this.userService = userService;
    }

    @BeforeEach
    public void setUp() {
        messengers = messengerService.getAll();
    }

    @Test
    public void test_Injected_Component() {
        assertThat(messengerService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(messengers).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertTrue(messengerService.getAll().size() > 0,
                "Getting all messengers must be bigger than 0");

        assertEquals(messengerService.getAll(), messengers,
                "Sets of all messengers must be the same.");
    }

    @Test
    public void test_Valid_Create_Id() {
        long ownerId = 1L;
        long recipientId = 3L;

        Messenger expected = new Messenger();
        expected.setOwner(userService.readById(ownerId));
        expected.setRecipient(userService.readById(recipientId));

        Messenger actual = messengerService.create(ownerId, recipientId);
        expected.setId(actual.getId());

        assertTrue(messengers.size() < messengerService.getAll().size(),
                "After creating new messenger getALl method must contains one more messenger than messengers before");
        assertEquals(expected, actual,
                "After creating new messenger we must get identical objects.");
    }

    @Test
    public void test_Invalid_Create_Id() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> messengerService.create(0L, 2L),
                        "Here must be EntityNotFoundException because we have not owner(user) with id 0!"),

                () -> assertThrows(EntityNotFoundException.class, () -> messengerService.create(1L, 0L),
                        "Here must be EntityNotFoundException because we have not recipient(user) with id 0!"),

                () -> assertThrows(SameUsersException.class, () -> messengerService.create(1L, 1L),
                        "Here must be SameUsersException because we write the same users")
        );
    }

    @Test
    public void test_Valid_Create_Username() {
        long ownerId = 3L;
        String username = "garry.potter";

        Messenger expected = new Messenger();
        expected.setOwner(userService.readById(ownerId));
        expected.setRecipient(userService.readByUsername(username));

        Messenger actual = messengerService.create(ownerId, username);
        expected.setId(actual.getId());

        assertTrue(messengers.size() < messengerService.getAll().size(),
                "After creating new messenger getALl method must contains one more messenger than messengers before");
        assertEquals(expected, actual,
                "After creating new messenger we must get identical objects.");
    }

    @Test
    public void test_Invalid_Create_Username() {
        long ownerID = 3L;
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> messengerService.create(0L, "steve"),
                        "Here must be EntityNotFoundException because we have not owner(user) with id 0!"),

                () -> assertThrows(InvalidTextException.class, () -> messengerService.create(ownerID, "   "),
                        "Here must be InvalidTextException because username cannot be 'blank'"),

                () -> assertThrows(InvalidTextException.class, () -> messengerService.create(ownerID, null),
                        "Here must be InvalidTextException because username cannot be 'null'"),

                () -> assertThrows(SameUsersException.class, () -> messengerService.create(ownerID, "oil"),
                        "Here must be SameUsersException because we write the same users")
        );
    }

    @Test
    public void test_Valid_ReadById() {
        Messenger expected =  messengerService.create(1L, 2L);
        Messenger actual = messengerService.readById(expected.getId());

        assertEquals(expected, actual,
                "After reading by id`s users must be equal.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> messengerService.readById(0L),
                "Here must be EntityNotFoundException because we have not the messenger with id 0!");
    }

    @Test
    public void test_Valid_ReadByOwnerAndRecipient() {
        long ownerId = 3L;
        long recipientId = 2L;

        Messenger actual = messengerService.readByOwnerAndRecipient(ownerId, recipientId);
        Messenger expected = messengerService.readById(actual.getId());

        assertEquals(expected, actual,
                "After reading by owner and recipient users must be equal.");
    }

    @Test
    public void test_Invalid_ReadByOwnerAndRecipient() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> messengerService.readByOwnerAndRecipient(1L, 0L),
                        "Here must be EntityNotFoundException because we have not recipient(user) with id 0!"),
                () -> assertThrows(EntityNotFoundException.class, () -> messengerService.readByOwnerAndRecipient(0L, 1L),
                        "Here must be EntityNotFoundException because we have not owner(user) with id 0!")
        );
    }

    @Test
    public void test_Valid_Delete() {
        messengerService.delete(1L);

        assertTrue(messengers.size() > messengerService.getAll().size(),
                "After deleting messenger service collection must be smaller than before deleting");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> messengerService.delete(0L),
                "Here must be EntityNotFoundException because we have not messenger with id 0!");
    }
}
