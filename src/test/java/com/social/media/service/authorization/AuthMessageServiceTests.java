package com.social.media.service.authorization;

import com.social.media.model.entity.Message;
import com.social.media.service.MessageService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthMessageServiceTests {
    private final AuthMessageService authMessageService;
    private final MessageService messageService;

    @Autowired
    public AuthMessageServiceTests(AuthMessageService authMessageService, MessageService messageService) {
        this.authMessageService = authMessageService;
        this.messageService = messageService;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(authMessageService).isNotNull();
        AssertionsForClassTypes.assertThat(messageService).isNotNull();
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfMessengerAndMessengerContainsMessageWithoutAdmin_True() {
        long ownerId = 1L;
        String currentUsername = "skallet24";
        long messengerID = 2L;
        String messageId = messageService.getAll()
                .stream()
                .filter(message -> message.getMessengerId() == messengerID)
                .findAny()
                .orElse(new Message())
                .getId();

        assertTrue(authMessageService.isAuthAndUserSameAndUserOwnerOfMessengerAndMessengerContainsMessageWithoutAdmin(
                        ownerId, currentUsername, messengerID, messageId),
                "Here must be true, because auth user and user are sames, and user owner of messenger and messenger contain message!");
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfMessengerAndMessengerContainsMessageWithoutAdmin_False() {
        long ownerId = 1L;
        String currentUsername = "skallet24";
        long messengerID = 2L;
        String messageId = messageService.getAll()
                .stream()
                .filter(message -> message.getMessengerId() != messengerID)
                .findAny()
                .orElse(new Message())
                .getId();

        assertFalse(authMessageService.isAuthAndUserSameAndUserOwnerOfMessengerAndMessengerContainsMessageWithoutAdmin(
                        ownerId, currentUsername, messengerID, messageId),
                "Here must be false, because messenger not contain message!");
    }
}
