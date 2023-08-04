package com.social.media.repository;

import com.social.media.model.entity.Messenger;
import com.social.media.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MessengerRepositoryTests {
    private final MessengerRepository messengerRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessengerRepositoryTests(MessengerRepository messengerRepository, UserRepository userRepository) {
        this.messengerRepository = messengerRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void test_Injected_Component() {
        assertThat(messengerRepository).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindByOwnerIdAndRecipientId() {
        long ownerId = 1L;
        long recipientId = 2L;

        Messenger expected = new Messenger();
        expected.setOwner(userRepository.findById(ownerId).orElse(new User()));
        expected.setRecipient(userRepository.findById(recipientId).orElse(new User()));

        Messenger actual = messengerRepository.findByOwnerIdAndRecipientId(1L, 2L);
        expected.setId(actual.getId());

        assertEquals(expected, actual,
                "Messengers after reading by owner and recipient users must be equal!");
    }

    @Test
    public void test_Valid_FindAllByOwnerId() {
        long ownerId = 2L;
        User owner = userRepository.findById(ownerId).orElse(new User());

        List<Messenger> messengers = messengerRepository.findAllByOwnerId(ownerId);

        assertAll(
                () -> assertFalse(messengers.isEmpty(),
                        "User messengers list should contains one messenger!"),
                () -> assertTrue(messengers.size() < messengerRepository.findAll().size(),
                        "All messengers size must be bigger than user messengers."),
                () -> assertEquals(messengers.size(), owner.getMyMessengers().size(),
                        "Messengers that user create must be the same messengers that reads by repository.")
        );
    }

    @Test
    public void test_Invalid_FindAllByOwnerId() {
        assertTrue(messengerRepository.findAllByOwnerId(0L).isEmpty(),
                "We have no user with id 0, so here must be empty list.");
    }
}
