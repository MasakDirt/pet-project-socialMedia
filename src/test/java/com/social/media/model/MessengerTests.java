package com.social.media.model;

import com.social.media.model.entity.Messenger;
import com.social.media.model.entity.User;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static com.social.media.model.ValidatorHelperForTests.getViolations;

@SpringBootTest
public class MessengerTests {
    private static Messenger messenger;

    @BeforeAll
    public static void init() {
        messenger = new Messenger();
        messenger.setId(1L);
        messenger.setOwner(new User());
        messenger.setRecipient(new User());
    }

    @Test
    public void test_Valid_Messenger(){
        Set<ConstraintViolation<Messenger>> violations = getViolations(messenger);

        Assertions.assertEquals(0, violations.size());
    }
}
