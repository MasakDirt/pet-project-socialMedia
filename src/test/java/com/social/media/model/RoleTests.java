package com.social.media.model;

import com.social.media.model.entity.Role;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Stream;

import static com.social.media.model.ValidatorHelperForTests.getViolations;

@SpringBootTest
public class RoleTests {
    private static Role validRole;

    @BeforeAll
    public static void init() {
        validRole = new Role();
        validRole.setName("VALID");
    }

    @Test
    public void test_Valid_Role() {
        Set<ConstraintViolation<Role>> violations = getViolations(validRole);

        Assertions.assertEquals(0, violations.size());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRoleName")
    public void test_Invalid_Role_Name(String name, String error) {
        Role invalid = new Role();
        invalid.setName(name);

        Set<ConstraintViolation<Role>> violations = getViolations(invalid);

        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidRoleName() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("Invalid", "Invalid"),
                Arguments.of("InvaliD", "InvaliD"),
                Arguments.of("invalid", "invalid"),
                Arguments.of(null, null)
        );
    }
}
