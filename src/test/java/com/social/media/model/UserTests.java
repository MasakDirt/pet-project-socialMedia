package com.social.media.model;

import com.social.media.model.entity.User;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Stream;

import static com.social.media.model.ValidatorHelperForTests.getViolations;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTests {
    private static User validUser;

    @BeforeAll
    public static void init() {
        validUser = new User();
        validUser.setEmail("user@mail.co");
        validUser.setFirstName("User");
        validUser.setLastName("Userslast");
        validUser.setPassword("validPass");
        validUser.setUsername("users");
    }

    @Test
    public void test_Valid_User() {
        Set<ConstraintViolation<User>> violations = getViolations(validUser);

        assertEquals(0, violations.size());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEmail")
    public void test_Invalid_User_Email(String email, String error){
        User invalid = new User();
        invalid.setUsername(validUser.getUsername());
        invalid.setPassword(validUser.getPassword());
        invalid.setLastName(validUser.getLastName());
        invalid.setFirstName(validUser.getFirstName());

        invalid.setEmail(email);

        Set<ConstraintViolation<User>> violations = getViolations(invalid);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidEmail(){
        return Stream.of(
                Arguments.of("",""),
                Arguments.of(null,null),
                Arguments.of("notvalid","notvalid"),
                Arguments.of("notValid","notValid"),
                Arguments.of("not@valid","not@valid"),
                Arguments.of("not@valid.","not@valid.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFirstName")
    public void test_Invalid_User_FirstName(String firstName, String error){
        User invalid = new User();
        invalid.setEmail(validUser.getEmail());
        invalid.setUsername(validUser.getUsername());
        invalid.setPassword(validUser.getPassword());
        invalid.setLastName(validUser.getLastName());

        invalid.setFirstName(firstName);

        //the last name as a first name!
        Set<ConstraintViolation<User>> violations = getViolations(invalid);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidFirstName(){
        return Stream.of(
                Arguments.of("",""),
                Arguments.of(null,null),
                Arguments.of("notvalid","notvalid"),
                Arguments.of("notValid","notValid"),
                Arguments.of("notvaliD","notvaliD")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPassword")
    public void test_Invalid_User_Password(String password, String error){
        User invalid = new User();
        invalid.setEmail(validUser.getEmail());
        invalid.setUsername(validUser.getUsername());
        invalid.setLastName(validUser.getLastName());
        invalid.setFirstName(validUser.getFirstName());

        invalid.setPassword(password);

        Set<ConstraintViolation<User>> violations = getViolations(invalid);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidPassword(){
        return Stream.of(
                Arguments.of("",""),
                Arguments.of(null,null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUsername")
    public void test_Invalid_User_Username(String username, String error){
        User invalid = new User();
        invalid.setEmail(validUser.getEmail());
        invalid.setLastName(validUser.getLastName());
        invalid.setFirstName(validUser.getFirstName());
        invalid.setPassword(validUser.getPassword());

        invalid.setUsername(username);

        Set<ConstraintViolation<User>> violations = getViolations(invalid);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidUsername(){
        return Stream.of(
                Arguments.of("",""),
                Arguments.of("Invalid","Invalid"),
                Arguments.of("inValid","inValid"),
                Arguments.of("invalid♡♡♡","invalid♡♡♡"),
                Arguments.of("invalid@","invalid@"),
                Arguments.of("invalid$","invalid$"),
                Arguments.of(null,null)
        );
    }
}
