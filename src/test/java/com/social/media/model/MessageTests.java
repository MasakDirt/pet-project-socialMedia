package com.social.media.model;

import com.social.media.model.entity.Message;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static com.social.media.model.ValidatorHelperForTests.getViolations;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessageTests {
    private static Message validMessage;

    @BeforeAll
    public static void init() {
        validMessage = new Message();
        validMessage.setMessage("Hello!");
        validMessage.setTimestamp(LocalDateTime.now());
        validMessage.setMessengerId(2L);
    }

    @Test
    public void test_Valid_Message() {
        Set<ConstraintViolation<Message>> violations = getViolations(validMessage);
        assertEquals(0, violations.size());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidMessageField")
    public void test_Invalid_MessageField(String message, String error) {
        Message invalid = new Message();
        invalid.setMessage(message);
        invalid.setMessengerId(4L);
        invalid.setTimestamp(LocalDateTime.now());

        Set<ConstraintViolation<Message>> violations = getViolations(invalid);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    @Test
    public void test_Invalid_LocalDateTime() {
        Message invalid = new Message();
        invalid.setMessage("message");
        invalid.setMessengerId(5L);
        invalid.setTimestamp(null);

        Set<ConstraintViolation<Message>> violations = getViolations(invalid);
        assertEquals(1, violations.size());
    }

    private static Stream<Arguments> provideInvalidMessageField() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of(null, null)
        );
    }
}
