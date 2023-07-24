package com.social.media.repository;

import com.social.media.model.entity.Messenger;
import com.social.media.model.entity.User;
import com.social.media.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MessengerRepositoryTests {
    private final MessengerRepository messengerRepository;
    private final UserService userService;

    @Autowired
    public MessengerRepositoryTests(MessengerRepository messengerRepository, UserService userService) {
        this.messengerRepository = messengerRepository;
        this.userService = userService;
    }

    @Test
    public void test_Injected_Component() {
        assertThat(messengerRepository).isNotNull();
    }

    @Test
    public void test_FindByOwnerAndRecipient() {
        User owner = userService.readById(1L);
        User recipient = userService.readById(2L);

        Messenger expected = new Messenger();
        expected.setOwner(owner);
        expected.setRecipient(recipient);

        Messenger actual = messengerRepository.findByOwnerAndRecipient(owner, recipient);
        expected.setId(actual.getId());

        Assertions.assertEquals(expected, actual,
                "Messengers after reading by owner and recipient users must be equal!");
    }
}
