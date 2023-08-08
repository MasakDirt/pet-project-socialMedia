package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserServiceTests {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    private List<User> users;

    @Autowired
    public UserServiceTests(UserService userService, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @BeforeEach
    public void setUsers() {
        users = userService.getAll();
    }

    @Test
    public void test_Injected_Component() {
        assertThat(userService).isNotNull();
        assertThat(passwordEncoder).isNotNull();
        assertThat(roleService).isNotNull();
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

        User actual = userService.create(expected, roleService.readByName("USER"));
        expected.setId(actual.getId());

        assertTrue(users.size() < userService.getAll().size(),
                "After creating new user, getAll method must contain one more.");
        assertEquals(expected, actual,
                "Created object and written in method, must be equal!");
    }

    @Test
    public void test_Invalid_Create() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.create(null, roleService.readByName("USER")),
                        "Here must be IllegalArgumentException because we cannot pass the null!"),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.create(new User(), roleService.readByName("USER")),
                        "Here must be IllegalArgumentException because we cannot pass new User without initialize fields.")
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
        expected = userService.create(expected, roleService.readByName("USER"));

        User actual = userService.readById(expected.getId());

        assertEquals(expected, actual,
                "After reading by id, objects must be equal.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> userService.readById(0L),
                "Here must be EntityNotFoundException because we have not user with id 0.");
    }

    @ParameterizedTest
    @MethodSource("argumentsForReadByIdOrUsernameOrEmail")
    public void test_Valid_ReadByIdOrUsernameOrEmail(long ownerId, String username, String email) {
        User expected = new User();
        expected.setEmail("olivia@mail.co");
        expected.setUsername("oil");
        expected.setFirstName("Olivia");
        expected.setLastName("Jones");

        User actual = userService.readByIdOrUsernameOrEmail(ownerId, username, email);
        expected.setId(actual.getId());
        expected.setPassword(actual.getPassword());

        assertEquals(expected, actual,
                "It test should return true, because users fully same!");
    }

    private static Stream<Arguments> argumentsForReadByIdOrUsernameOrEmail() {
        return Stream.of(
                Arguments.of(3L, "invalid.username", "invalid@mail.co"),
                Arguments.of(0L, "oil", "invalid@mail.co"),
                Arguments.of(0L, "invalid.username", "olivia@mail.co")
        );
    }

    @Test
    public void test_Invalid_ReadByIdOrUsernameOrEmail() {
        assertThrows(EntityNotFoundException.class, () -> userService.readByIdOrUsernameOrEmail(0L, "", ""),
                "We have no user with 0 id or empty username or email, so here must be EntityNotFoundException");
    }

    @Test
    public void test_Valid_Update() {
        long userId = 2L;
        String newPassword = "UpdatedPass";

        User user = userService.readById(userId);

        User expected = new User();
        expected.setId(userId);
        expected.setFirstName(user.getFirstName());
        expected.setLastName(user.getLastName());
        expected.setUsername(user.getUsername());
        expected.setEmail(user.getEmail());
        expected.setPassword(user.getPassword());

        String oldFirstName = expected.getFirstName();
        String oldLastName = expected.getLastName();
        String oldUsername = expected.getUsername();
        String oldEmail = expected.getEmail();

        final String oldPassword = expected.getPassword();

        expected.setPassword(newPassword);

        User actual = userService.update(expected, "2222");

        assertAll(
                () -> assertEquals(oldFirstName, actual.getFirstName(),
                        "After updating users field`s first name must be the same."),
                () -> assertEquals(oldLastName, actual.getLastName(),
                        "After updating users field`s last name must be the same."),
                () -> assertEquals(oldUsername, actual.getUsername(),
                        "After updating users field`s username must be the same."),
                () -> assertEquals(oldEmail, actual.getEmail(),
                        "After updating users field`s email must be the same."),

                () -> assertNotEquals(oldPassword, actual.getPassword(),
                        "After updating users field`s old password must be different.")
        );
    }

    @Test
    public void test_Invalid_Update() {
        var user = userService.readById(2L);
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.update(null, "pass"),
                        "Here must be IllegalArgumentException because we cannot pass the null!"),

                () -> assertThrows(EntityNotFoundException.class, () -> userService.update(new User(), "pass"),
                        "Here must be EntityNotFoundException because we cannot pass new User without initialize fields " +
                                "and in this example we have an 0 id."),

                () -> assertThrows(InvalidTextException.class, () -> userService.update(user, "  "),
                        "Here must be InvalidTextException because we cannot pass the blank password!"),

                () -> assertThrows(InvalidTextException.class, () -> userService.update(user, null),
                        "Here must be InvalidTextException because we cannot pass the null password!")
        );
    }

    @Test
    public void test_Valid_UpdateNamesById() {
        long userId = 2L;
        String newFirstName = "New first name";
        String newLastName = "New last name";

        User user = userService.readById(userId);

        User expected = new User();
        expected.setId(userId);
        expected.setFirstName(user.getFirstName());
        expected.setLastName(user.getLastName());
        expected.setUsername(user.getUsername());
        expected.setEmail(user.getEmail());
        expected.setPassword(user.getPassword());

        String oldUsername = expected.getUsername();
        String oldEmail = expected.getEmail();
        String oldPassword = expected.getPassword();

        User actual = userService.updateNamesById(userId, newFirstName, newLastName);

        assertAll(
                () -> assertEquals(userId, actual.getId(),
                        "After updating users field`s id must be the same."),
                () -> assertEquals(oldUsername, actual.getUsername(),
                        "After updating users field`s username must be the same."),
                () -> assertEquals(oldEmail, actual.getEmail(),
                        "After updating users field`s email must be the same."),
                () -> assertEquals(oldPassword, actual.getPassword(),
                        "After updating users field`s password must be the same."),

                () -> assertEquals(newFirstName, actual.getFirstName(),
                        "After updating users field`s first name must be the same."),
                () -> assertEquals(newLastName, actual.getLastName(),
                        "After updating users field`s last name must be the same.")
        );
    }

    @Test
    public void test_Invalid_UpdateNamesById() {
        long ownerId = 2L;
        String firstName = "First";
        String lastName = "Last";

        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesById(ownerId, firstName, "   "),
                        "Last name cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesById(ownerId, firstName, null),
                        "Last name cannot be null, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesById(ownerId, "  ", lastName),
                        "First name cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesById(ownerId, null, lastName),
                        "First name cannot be null, so here must be InvalidTextException!"),
                () -> assertThrows(EntityNotFoundException.class, () -> userService.updateNamesById(0L, firstName, lastName),
                        "We have no user with id 0, so here must be EntityNotFoundException!")
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsForUpdateNamesByUsernameOrEmail")
    public void test_Valid_UpdateNamesByUsernameOrEmail(String usernameOrEmail) {
        String newFirstName = "New first name";
        String newLastName = "New last name";

        User user = userService.getUserByUsernameOrEmail(usernameOrEmail);

        User expected = new User();

        expected.setFirstName(user.getFirstName());
        expected.setLastName(user.getLastName());
        expected.setUsername(user.getUsername());
        expected.setEmail(user.getEmail());
        expected.setPassword(user.getPassword());

        String oldUsername = expected.getUsername();
        String oldEmail = expected.getEmail();
        String oldPassword = expected.getPassword();

        User actual = userService.updateNamesByUsernameOrEmail(usernameOrEmail, newFirstName, newLastName);
        expected.setId(actual.getId());

        assertAll(
                () -> assertEquals(oldUsername, actual.getUsername(),
                        "After updating users field`s username must be the same."),
                () -> assertEquals(oldEmail, actual.getEmail(),
                        "After updating users field`s email must be the same."),
                () -> assertEquals(oldPassword, actual.getPassword(),
                        "After updating users field`s password must be the same."),

                () -> assertEquals(newFirstName, actual.getFirstName(),
                        "After updating users field`s first name must be the same."),
                () -> assertEquals(newLastName, actual.getLastName(),
                        "After updating users field`s last name must be the same.")
        );
    }

    private static Stream<Arguments> argumentsForUpdateNamesByUsernameOrEmail() {
        return Stream.of(
                Arguments.of("oil"),
                Arguments.of("olivia@mail.co")
        );
    }

    @Test
    public void test_Invalid_UpdateNamesByUsernameOrEmail() {
        String usernameOrEmail = "username or email";

        String firstName = "First";
        String lastName = "Last";

        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesByUsernameOrEmail(usernameOrEmail, firstName, "   "),
                        "Last name cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesByUsernameOrEmail(usernameOrEmail, firstName, null),
                        "Last name cannot be null, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesByUsernameOrEmail(usernameOrEmail, "  ", lastName),
                        "First name cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesByUsernameOrEmail(usernameOrEmail, null, lastName),
                        "First name cannot be null, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesByUsernameOrEmail("  ", firstName, lastName),
                        "Username or email can not be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updateNamesByUsernameOrEmail(null, firstName, lastName),
                        "Username or email can not be null, so here must be InvalidTextException!"),
                () -> assertThrows(EntityNotFoundException.class, () -> userService.updateNamesByUsernameOrEmail(usernameOrEmail, firstName, lastName),
                        "We have no in db user with this username or email, so here must be EntityNotFoundException")
        );
    }

    @Test
    public void test_Valid_UpdatePasswordById() {
        long userId = 1L;
        String newPassword = "UpdatedPass";

        User user = userService.readById(userId);

        User expected = new User();
        expected.setId(userId);
        expected.setFirstName(user.getFirstName());
        expected.setLastName(user.getLastName());
        expected.setUsername(user.getUsername());
        expected.setEmail(user.getEmail());
        expected.setPassword(user.getPassword());

        String oldFirstName = expected.getFirstName();
        String oldLastName = expected.getLastName();
        String oldUsername = expected.getUsername();
        String oldEmail = expected.getEmail();

        final String oldPassword = expected.getPassword();

        expected.setPassword(newPassword);

        User actual = userService.updatePasswordById(userId, "1111", newPassword);
        expected.setId(actual.getId());

        assertAll(
                () -> assertEquals(userId, actual.getId(),
                        "After updating users field`s id must be the same."),
                () -> assertEquals(oldFirstName, actual.getFirstName(),
                        "After updating users field`s first name must be the same."),
                () -> assertEquals(oldLastName, actual.getLastName(),
                        "After updating users field`s last name must be the same."),
                () -> assertEquals(oldUsername, actual.getUsername(),
                        "After updating users field`s username must be the same."),
                () -> assertEquals(oldEmail, actual.getEmail(),
                        "After updating users field`s email must be the same."),

                () -> assertNotEquals(oldPassword, actual.getPassword(),
                        "After updating users field`s old password must be different.")
        );
    }

    @Test
    public void test_Invalid_UpdatePasswordById() {
        long ownerId = 2L;
        String oldPass = "old";
        String newPass = "new";

        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordById(ownerId, oldPass, "   "),
                        "Old password cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordById(ownerId, oldPass, null),
                        "Old password cannot be null, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordById(ownerId, "  ", newPass),
                        "New password cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordById(ownerId, null, newPass),
                        "New password cannot be null, so here must be InvalidTextException!"),
                () -> assertThrows(EntityNotFoundException.class, () -> userService.updatePasswordById(0L, oldPass, newPass),
                        "We have no user with id 0, so here must be EntityNotFoundException!")
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsForUpdatePasswordByUsernameOrEmail")
    public void test_Valid_UpdatePasswordByUsernameOrEmail(String usernameOrEmail) {
        String newPassword = "UpdatedPass";

        User user = userService.getUserByUsernameOrEmail(usernameOrEmail);

        User expected = new User();
        expected.setFirstName(user.getFirstName());
        expected.setLastName(user.getLastName());
        expected.setUsername(user.getUsername());
        expected.setEmail(user.getEmail());
        expected.setPassword(user.getPassword());

        String oldFirstName = expected.getFirstName();
        String oldLastName = expected.getLastName();
        String oldUsername = expected.getUsername();
        String oldEmail = expected.getEmail();

        final String oldPassword = expected.getPassword();

        expected.setPassword(newPassword);

        User actual = userService.updatePasswordByUsernameOrEmail(usernameOrEmail, "3333", newPassword);
        expected.setId(actual.getId());

        assertAll(
                () -> assertEquals(oldFirstName, actual.getFirstName(),
                        "After updating users field`s first name must be the same."),
                () -> assertEquals(oldLastName, actual.getLastName(),
                        "After updating users field`s last name must be the same."),
                () -> assertEquals(oldUsername, actual.getUsername(),
                        "After updating users field`s username must be the same."),
                () -> assertEquals(oldEmail, actual.getEmail(),
                        "After updating users field`s email must be the same."),

                () -> assertNotEquals(oldPassword, actual.getPassword(),
                        "After updating users field`s old password must be different.")
        );
    }

    private static Stream<Arguments> argumentsForUpdatePasswordByUsernameOrEmail() {
        return Stream.of(
                Arguments.of("olivia@mail.co"),
                Arguments.of("oil")
        );
    }

    @Test
    public void test_Invalid_UpdatePasswordByUsernameOrEmail() {
        String usernameOrEmail = "username or email";

        String oldPass = "old";
        String newPass = "new";

        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordByUsernameOrEmail(usernameOrEmail, oldPass, "   "),
                        "Old password cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordByUsernameOrEmail(usernameOrEmail, oldPass, null),
                        "Old password cannot be null, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordByUsernameOrEmail(usernameOrEmail, "  ", newPass),
                        "New password cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordByUsernameOrEmail(usernameOrEmail, null, newPass),
                        "New password cannot be null, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordByUsernameOrEmail("  ", oldPass, newPass),
                        "Username or email can not be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.updatePasswordByUsernameOrEmail(null, oldPass, newPass),
                        "Username or email can not be null, so here must be InvalidTextException!"),
                () -> assertThrows(EntityNotFoundException.class, () -> userService.updatePasswordByUsernameOrEmail(usernameOrEmail, oldPass, newPass),
                        "We have no in db user with this username or email, so here must be EntityNotFoundException")
        );
    }

    @Test
    public void test_Valid_Delete_ById() {
        userService.delete(1L);

        assertTrue(users.size() > userService.getAll().size(),
                "Users list before must be bigger than after");
    }

    @Test
    public void test_Invalid_Delete_ById() {
        assertThrows(EntityNotFoundException.class, () -> userService.delete(0L),
                "Here must be EntityNotFoundException because we have not user with id 0.");
    }

    @Test
    public void test_Valid_Delete_ByUsername() {
        userService.delete("oil");

        assertTrue(users.size() > userService.getAll().size(),
                "Users list before must be bigger than after");
    }

    @Test
    public void test_Valid_Delete_ByEmail() {
        userService.delete("jone@mail.co");

        assertTrue(users.size() > userService.getAll().size(),
                "Users list before must be bigger than after");
    }

    @Test
    public void test_Invalid_Delete_ByUsernameOrEmail() {
        assertThrows(InvalidTextException.class, () -> userService.delete(""),
                "Here must be InvalidTextException because we have not user with empty username or email.");
    }

    @ParameterizedTest
    @MethodSource("argumentsForByIdOrUsernameOrEmail")
    public void test_Valid_Delete_ByIdOrUsernameOrEmail(long ownerId, String username, String email) {
        userService.delete(ownerId, username, email);

        assertTrue(users.size() > userService.getAll().size(),
                "Users list before must be bigger than after");
    }

    private static Stream<Arguments> argumentsForByIdOrUsernameOrEmail() {
        return Stream.of(
                Arguments.of(1L, "invalid", "invalid"),
                Arguments.of(0L, "oil", "invalid"),
                Arguments.of(0L, "invalid", "garry@mail.co")
        );
    }

    @Test
    public void test_Invalid_Delete_ByIdOrUsernameOrEmail() {
        assertThrows(EntityNotFoundException.class, () -> userService.delete(0L, "", ""),
                "We have no user with id 0 or empty username or email, so here must be EntityNotFoundException!");
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
        expected = userService.create(expected, roleService.readByName("USER"));

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
        expected = userService.create(expected, roleService.readByName("USER"));

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

        List<User> expected = List.of(userService.create(user1, roleService.readByName("USER")), userService.create(user2, roleService.readByName("USER")));

        assertEquals(expected, userService.getAllByFirstName(firstName),
                "Users which was read by name, should be the same.");
    }

    @Test
    public void test_Invalid_GetAllByFirstName_UserWithThatNameDoesNotExist() {
        assertTrue(userService.getAllByFirstName("Notfound").isEmpty(),
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

        List<User> expected = List.of(userService.create(user1, roleService.readByName("USER")), userService.create(user2, roleService.readByName("USER")));

        assertEquals(expected, userService.getAllByLastName(lastName),
                "Users which was read by name, should be the same.");
    }

    @Test
    public void test_Invalid_GetAllByLastName_UserWithThatNameDoesNotExist() {
        assertTrue(userService.getAllByLastName("Notfound").isEmpty(),
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

    @ParameterizedTest
    @MethodSource("argumentsForGetUserByUsernameOrEmail")
    public void test_Valid_GetUserByUsernameOrEmail(String currentUsernameOrEmail) {
        User expected = userService.readById(1L);
        User actual = userService.getUserByUsernameOrEmail(currentUsernameOrEmail);

        assertEquals(expected, actual,
                "User that read by id must be equal with user that read by username or email!");
    }

    private static Stream<Arguments> argumentsForGetUserByUsernameOrEmail() {
        return Stream.of(
                Arguments.of("skallet24"),
                Arguments.of("jone@mail.co")
        );
    }

    @Test
    public void test_Invalid_GetUserByUsernameOrEmail() {
        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> userService.getUserByUsernameOrEmail("  "),
                        "Here must be InvalidTextException because we cannot pass an empty username or email!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.getUserByUsernameOrEmail(null),
                        "Here must be InvalidTextException because we cannot pass null username or email!"),
                () -> assertThrows(EntityNotFoundException.class, () -> userService.getUserByUsernameOrEmail("invalid"),
                        "Here must be EntityNotFoundException because we have not user with that username or email!")
        );
    }

    @Test
    public void test_Valid_GetAllByRole() {
        String roleName = "USER";
        List<User> expectedUsers = userService.getAll()
                .stream()
                .filter(user -> user.getRole().getName().equals(roleName))
                .toList();

        List<User> actualUsers = userService.getAllByRole(roleName);

        assertAll(
                () -> assertFalse(actualUsers.isEmpty(),
                        "List os users with same role names can not be empty(with this role name)!"),
                () -> assertTrue(actualUsers.size() < users.size(),
                        "All users size of course must be bigger than users with same role name"),
                () -> assertEquals(expectedUsers.size(), actualUsers.size(),
                        "List of users with same role names must be equal!")
        );
    }

    @Test
    public void test_Invalid_GetAllByRole() {
        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> userService.getAllByRole(""),
                        "Role name cannot be blank, so here must be InvalidTextException!"),
                () -> assertThrows(InvalidTextException.class, () -> userService.getAllByRole(null).isEmpty(),
                        "Role name cannot be null, so here must be InvalidTextException!"),
                () -> assertTrue(userService.getAllByRole("INVALID").isEmpty(),
                        "We have no users with this role name, so here must be true")
        );
    }
}
