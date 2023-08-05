package com.social.media.service.authorization;

import com.social.media.model.entity.Messenger;
import com.social.media.service.MessengerService;
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
public class AuthMessengerServiceTests {
    private final AuthMessengerService authMessengerService;
    private final MessengerService messengerService;

    @Autowired
    public AuthMessengerServiceTests(AuthMessengerService authMessengerService, MessengerService messengerService) {
        this.authMessengerService = authMessengerService;
        this.messengerService = messengerService;
    }

    @Test
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(authMessengerService).isNotNull();
        AssertionsForClassTypes.assertThat(messengerService).isNotNull();
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin_True() {
        long ownerId = 1L;
        String currentUsername = "skallet24";
        long messengerID = 2L;

        assertTrue(authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(ownerId, currentUsername, messengerID),
                "Here must be true, because auth user and user are sames, and user owner of messenger.");
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin_False_Username() {
        long ownerId = 1L;
        String currentUsername = "oil";
        long messengerID = 2L;

        assertFalse(authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(ownerId, currentUsername, messengerID),
                "Here must be false, because auth user and user are not sames.");
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin_False_OwnerId() {
        long ownerId = 2L;
        String currentUsername = "skallet24";
        long messengerID = 2L;

        assertFalse(authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(ownerId, currentUsername, messengerID),
                "Here must be false, because auth user and user are not sames and user is not owner of messenger.");
    }

    @Test
    public void test_isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin_False_MessengerId() {
        long ownerId = 1L;
        String currentUsername = "skallet24";
        long messengerID = 3L;

        assertFalse(authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(ownerId, currentUsername, messengerID),
                "Here must be false, because user is not owner of messenger.");
    }

    @Test
    public void test_GetMessenger() {
        long messengerId = 5L;

        Messenger expected = messengerService.readById(messengerId);
        Messenger actual = authMessengerService.getMessenger(messengerId);

        assertEquals(expected, actual,
                "As we has the same id, so messengers must be the same, too!");
    }
}
