package com.social.media.repository;

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
public class UserRepositoryTests {
    private final UserRepository userRepository;
    private User expected;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void test_Injected_Component() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindByUsername() {
        String username = "to.find";
        expected = new User();
        expected.setUsername(username);
        expected.setEmail("for@mail.co");
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setPassword("newPass");

        expected = userRepository.save(expected);

        User actual = userRepository.findByUsername(username).orElse(new User());
        assertEquals(expected, actual,
                "Users after reading by username must be equal!");
    }

    @Test
    public void test_Invalid_FindByUsername() {
        assertEquals(new User(), userRepository.findByUsername("").orElse(new User()),
                "We have no user with empty username, so here must be new User object.");
    }

    @Test
    public void test_Valid_FindByEmail() {
        String email = "for@mail.co";
        expected = new User();
        expected.setUsername("username");
        expected.setEmail(email);
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setPassword("newPass");

        expected = userRepository.save(expected);

        User actual = userRepository.findByEmail(email).orElse(new User());
        assertEquals(expected, actual,
                "Users after reading by email must be equal!");
    }

    @Test
    public void test_Invalid_FindByEmail() {
        assertEquals(new User(), userRepository.findByEmail("").orElse(new User()),
                "We have no user with empty email, so here must be new User object.");
    }

    @Test
    public void test_Valid_FindByUsernameOrEmail_ByUsername() {
        String username = "garry.potter";

        User expectedByUsername = new User();
        expectedByUsername.setUsername(username);
        expectedByUsername.setEmail("garry@mail.co");
        expectedByUsername.setFirstName("Garry");
        expectedByUsername.setLastName("Thomas");

        User actualByUsername = userRepository.findByUsernameOrEmail(username, "invalid").orElse(new User());
        expectedByUsername.setId(actualByUsername.getId());
        expectedByUsername.setPassword(actualByUsername.getPassword());

        assertAll(
                () -> assertEquals(expectedByUsername.getUsername(), username,
                        "User username must be equal to written!"),
                () -> assertEquals(expectedByUsername, actualByUsername,
                        "Users that created and read must be equal")
        );
    }

    @Test
    public void test_Valid_FindByUsernameOrEmail_ByEmail() {
        String email = "jone@mail.co";

        User expectedByEmail = new User();
        expectedByEmail.setUsername("skallet24");
        expectedByEmail.setEmail(email);
        expectedByEmail.setFirstName("Garry");
        expectedByEmail.setLastName("Jones");

        User actualByEmail = userRepository.findByUsernameOrEmail("invalid", email).orElse(new User());
        expectedByEmail.setId(actualByEmail.getId());
        expectedByEmail.setPassword(actualByEmail.getPassword());

        assertAll(
                () -> assertEquals(expectedByEmail.getEmail(), email,
                        "User email must be equal to written!"),
                () -> assertEquals(expectedByEmail, actualByEmail,
                        "Users that created and read must be equal")
        );
    }

    @Test
    public void test_Invalid_FindByUsernameOrEmail() {
        assertEquals(new User(), userRepository.findByUsernameOrEmail("", "").orElse(new User()),
                "We have no users with empty username or email, so here must be new User object! ");
    }

    @Test
    public void test_Valid_FindByIdOrUsernameOrEmail_ById() {
        long id = 3L;

        User expectedById = new User();
        expectedById.setId(id);
        expectedById.setUsername("oil");
        expectedById.setEmail("olivia@mail.co");
        expectedById.setFirstName("Olivia");
        expectedById.setLastName("Jones");

        User actualById = userRepository.findByIdOrUsernameOrEmail(id, "invalid", "invalid").orElse(new User());
        expectedById.setPassword(actualById.getPassword());

        assertAll(
                () -> assertEquals(expectedById.getId(), id,
                        "User id must be equal to written!"),
                () -> assertEquals(expectedById, actualById,
                        "Users that created and read must be equal")
        );
    }

    @Test
    public void test_Valid_FindByIdOrUsernameOrEmail_ByUsername() {
        String username = "garry.potter";

        User expectedByUsername = new User();
        expectedByUsername.setUsername(username);
        expectedByUsername.setEmail("garry@mail.co");
        expectedByUsername.setFirstName("Garry");
        expectedByUsername.setLastName("Thomas");

        User actualByUsername = userRepository.findByIdOrUsernameOrEmail(0L, username, "invalid").orElse(new User());
        expectedByUsername.setId(actualByUsername.getId());
        expectedByUsername.setPassword(actualByUsername.getPassword());

        assertAll(
                () -> assertEquals(expectedByUsername.getUsername(), username,
                        "User username must be equal to written!"),
                () -> assertEquals(expectedByUsername, actualByUsername,
                        "Users that created and read must be equal")
        );
    }

    @Test
    public void test_Valid_FindByIdOrUsernameOrEmail_ByEmail() {
        String email = "jone@mail.co";

        User expectedByEmail = new User();
        expectedByEmail.setUsername("skallet24");
        expectedByEmail.setEmail(email);
        expectedByEmail.setFirstName("Garry");
        expectedByEmail.setLastName("Jones");

        User actualByEmail = userRepository.findByIdOrUsernameOrEmail(0L, "invalid", email).orElse(new User());
        expectedByEmail.setId(actualByEmail.getId());
        expectedByEmail.setPassword(actualByEmail.getPassword());

        assertAll(
                () -> assertEquals(expectedByEmail.getEmail(), email,
                        "User email must be equal to written!"),
                () -> assertEquals(expectedByEmail, actualByEmail,
                        "Users that created and read must be equal")
        );
    }

    @Test
    public void test_Invalid_FindByIdOrUsernameOrEmail() {
        assertEquals(new User(), userRepository.findByIdOrUsernameOrEmail(0L, "", "").orElse(new User()),
                "We have no users with id 0 or empty username or email, so here must be new User object! ");
    }

    @Test
    public void test_Valid_FindAllByLastName() {
        String lastName = "Jones";
        List<User> expectedUsers = userRepository.findAll()
                .stream()
                .filter(user -> user.getLastName().equals(lastName))
                .toList();

        List<User> actualUsers = userRepository.findAllByLastName(lastName);

        assertAll(
                () -> assertFalse(actualUsers.isEmpty(),
                        "List os users with same last names can not be empty(with this last name)!"),
                () -> assertTrue(actualUsers.size() < userRepository.findAll().size(),
                        "All users size of course must be bigger than users with same last name"),
                () -> assertEquals(expectedUsers, actualUsers,
                        "List of users with same last names must be equal!")
        );
    }

    @Test
    public void test_Invalid_FindAllByLastName() {
        assertTrue(userRepository.findAllByLastName("").isEmpty(),
                "We have no user with empty last name, so here must be true");
    }

    @Test
    public void test_Valid_FindAllByFirstName() {
        String firstName = "Garry";
        List<User> expectedUsers = userRepository.findAll()
                .stream()
                .filter(user -> user.getFirstName().equals(firstName))
                .toList();

        List<User> actualUsers = userRepository.findAllByFirstName(firstName);

        assertAll(
                () -> assertFalse(actualUsers.isEmpty(),
                        "List os users with same first names can not be empty(with this first name)!"),
                () -> assertTrue(actualUsers.size() < userRepository.findAll().size(),
                        "All users size of course must be bigger than users with same first name"),
                () -> assertEquals(expectedUsers, actualUsers,
                        "List of users with same first names must be equal!")
        );
    }

    @Test
    public void test_Invalid_FindAllByFirstName() {
        assertTrue(userRepository.findAllByFirstName("").isEmpty(),
                "We have no user with empty first name, so here must be true");
    }

    @Test
    public void test_Valid_FindAllByRoleName() {
        String roleName = "USER";
        List<User> expectedUsers = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().getName().equals(roleName))
                .toList();

        List<User> actualUsers = userRepository.findAllByRoleName(roleName);

        assertAll(
                () -> assertFalse(actualUsers.isEmpty(),
                        "List os users with same role names can not be empty(with this role name)!"),
                () -> assertTrue(actualUsers.size() < userRepository.findAll().size(),
                        "All users size of course must be bigger than users with same role name"),
                () -> assertEquals(expectedUsers, actualUsers,
                        "List of users with same role names must be equal!")
        );
    }

    @Test
    public void test_Invalid_FindAllByRoleName() {
        assertTrue(userRepository.findAllByRoleName("").isEmpty(),
                "We have no user with empty role name, so here must be true");
    }
}
