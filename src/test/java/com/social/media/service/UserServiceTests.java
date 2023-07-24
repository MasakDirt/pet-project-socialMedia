package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserServiceTests {
    private final UserService userService;

    private Set<User> users;

    @Autowired
    public UserServiceTests(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    public void setUsers() {
        users = userService.getAll();
    }

    @Test
    public void test_Injected_Component() {
        assertThat(userService).isNotNull();
        assertThat(users).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertTrue(userService.getAll().size() > 0,
                "Size of all users must be bigger than 0.");

        assertEquals(userService.getAll(), users,
                "Set`s of users must be the same!");
    }

    @Test
    public void test_Valid_Create() {
        User expected = new User();
        expected.setPassword("newPass");
        expected.setEmail("mail@mail.co");
        expected.setUsername("new-user");
        expected.setFirstName("Peter");
        expected.setLastName("Nikolas");

        User actual = userService.create(expected);
        expected.setId(actual.getId());

        assertTrue(users.size() < userService.getAll().size(),
                "After creating new user, getAll method must contain one more.");
        assertEquals(expected, actual,
                "Created object and written in method, must be equal!");
    }

    @Test
    public void test_Invalid_Create() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.create(null),
                        "Here must be IllegalArgumentException because we cannot pass the null!"),
                () -> assertThrows(ConstraintViolationException.class, () -> userService.create(new User()),
                        "Here must be ConstraintViolationException because we cannot pass new User without initialize fields.")
        );
    }

    @Test
    public void test_Valid_ReadById() {
        User expected = new User();
        expected.setPassword("9856789Hjwe");
        expected.setEmail("mail2@mail.co");
        expected.setUsername("user-for-read");
        expected.setFirstName("User");
        expected.setLastName("Read");
        expected = userService.create(expected);

        User actual = userService.readById(expected.getId());

        assertEquals(expected, actual,
                "After reading by id, objects must be equal.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> userService.readById(0L),
                "Here must be EntityNotFoundException because we have not user with id 0.");
    }

    @Test
    public void test_Valid_Update() {
        String newPassword = "UpdatedPass";

        User expected = userService.readById(2L);
        String oldFirstName = expected.getFirstName();
        String oldLastName = expected.getLastName();
        String oldUsername = expected.getUsername();
        String oldEmail = expected.getEmail();
        String oldPassword = expected.getPassword();

        expected.setPassword(newPassword);

        User actual = userService.update(expected);

        assertAll(
                () -> assertEquals(oldFirstName, actual.getFirstName(),
                        "After updating users field`s first name must be the same."),
                () -> assertEquals(oldLastName, actual.getLastName(),
                        "After updating users field`s last name must be the same."),
                () -> assertEquals(oldUsername, actual.getUsername(),
                        "After updating users field`s username must be the same."),
                () -> assertEquals(oldEmail, actual.getEmail(),
                        "After updating users field`s email must be the same."),
                () -> assertEquals(newPassword, actual.getPassword(),
                        "After updating users field`s of new password must be the same."),

                () -> assertNotEquals(oldPassword, actual.getPassword(),
                        "After updating users field`s old password must be different.")
        );
    }

    @Test
    public void test_Invalid_Update() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.update(null),
                        "Here must be IllegalArgumentException because we cannot pass the null!"),
                () -> assertThrows(EntityNotFoundException.class, () -> userService.update(new User()),
                        "Here must be EntityNotFoundException because we cannot pass new User without initialize fields " +
                                "and in this example we have an 0 id.")
        );
    }

    @Test
    public void test_Valid_Delete() {
        userService.delete(1L);

        assertTrue(users.size() > userService.getAll().size(),
                "Users list before must be bigger han after");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> userService.delete(0L),
                "Here must be EntityNotFoundException because we have not user with id 0.");
    }

    @Test
    public void test_Valid_ReadByUsername() {
        String username = "example";

        User expected = new User();
        expected.setPassword("1234");
        expected.setEmail("mail3@mail.co");
        expected.setUsername(username);
        expected.setFirstName("Karim");
        expected.setLastName("Benzema");
        expected = userService.create(expected);

        User actual = userService.readByUsername(username);

        assertEquals(expected, actual,
                "After reading by username, if it correct, objects must be equal.");
    }

    @Test
    public void test_Invalid_ReadByUsername() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> userService.readByUsername("invalid"),
                        "Here must be EntityNotFoundException because we have not user with username 'invalid'"),
                () -> assertThrows(InvalidTextException.class, () -> userService.readByUsername("  "),
                        "Here must be InvalidTextException because we cannot pass an empty username"),
                () -> assertThrows(InvalidTextException.class, () -> userService.readByUsername(null),
                        "Here must be InvalidTextException because we cannot pass an empty(null) username")
        );
    }

    @Test
    public void test_Valid_ReadByEmail() {
        String email = "email@mail.com";

        User expected = new User();
        expected.setPassword("password");
        expected.setEmail(email);
        expected.setUsername("username");
        expected.setFirstName("Lora");
        expected.setLastName("Green");
        expected = userService.create(expected);

        User actual = userService.readByEmail(email);

        assertEquals(expected, actual,
                "After reading by email, if it correct, objects must be equal.");
    }

    @Test
    public void test_Invalid_ReadByEmail() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> userService.readByEmail("in@valid.co"),
                        "Here must be EntityNotFoundException because we have not user with email 'in@valid.co'"),
                () -> assertThrows(InvalidTextException.class, () -> userService.readByEmail("  "),
                        "Here must be InvalidTextException because we cannot pass an empty email"),
                () -> assertThrows(InvalidTextException.class, () -> userService.readByEmail(null),
                        "Here must be InvalidTextException because we cannot pass an empty(null) email")
        );
    }

    @Test
    public void test_Valid_GetAllByFirstName() {
        String firstName = "Expected";

        User user1 = new User();
        user1.setPassword("password0987");
        user1.setEmail("mail4@mail.co");
        user1.setUsername("username23");
        user1.setFirstName(firstName);
        user1.setLastName("Smith");

        User user2 = new User();
        user2.setPassword("popularPassword1234");
        user2.setEmail("mail5@mail.co");
        user2.setUsername("user.name");
        user2.setFirstName(firstName);
        user2.setLastName("William");

        List<User> expected = List.of(userService.create(user1), userService.create(user2));

        assertEquals(expected, userService.getAllByFirstName(firstName),
                "Users which was read by name, should be the same.");
    }

    @Test
    public void test_Invalid_GetAllByFirstName_UserWithThatNameDoesNotExist() {
        assertEquals(new ArrayList<>(), userService.getAllByFirstName("Notfound"),
                "If we have not a user with this first name, we should get an empty array list.");
    }

    @Test
    public void test_Invalid_GetAllByFirstName_Exceptions() {
        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> userService.getAllByFirstName("  "),
                        "Here must be InvalidTextException because we cannot pass an empty first name"),
                () -> assertThrows(InvalidTextException.class, () -> userService.getAllByFirstName(null),
                        "Here must be InvalidTextException because we cannot pass an empty(null) first name")
        );
    }

    @Test
    public void test_Valid_GetAllByLastName() {
        String lastName = "Expected";

        User user1 = new User();
        user1.setPassword("Lflfkenk242nkKFJ;lmg");
        user1.setEmail("mail6@mail.co");
        user1.setUsername("bmw");
        user1.setFirstName("Eva");
        user1.setLastName(lastName);

        User user2 = new User();
        user2.setPassword("JFBjldbgl;jlj234");
        user2.setEmail("mail7@mail.co");
        user2.setUsername("nameforcheck");
        user2.setFirstName("Kate");
        user2.setLastName(lastName);

        List<User> expected = List.of(userService.create(user1), userService.create(user2));

        assertEquals(expected, userService.getAllByLastName(lastName),
                "Users which was read by name, should be the same.");
    }

    @Test
    public void test_Invalid_GetAllByLastName_UserWithThatNameDoesNotExist() {
        assertEquals(new ArrayList<>(), userService.getAllByLastName("Notfound"),
                "If we have not a user with this last name, we should get an empty array list.");
    }

    @Test
    public void test_Invalid_GetAllByLastName_Exceptions() {
        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> userService.getAllByLastName("  "),
                        "Here must be InvalidTextException because we cannot pass an empty last name"),
                () -> assertThrows(InvalidTextException.class, () -> userService.getAllByLastName(null),
                        "Here must be InvalidTextException because we cannot pass an empty(null) last name")
        );
    }
}
