package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.user.*;
import com.social.media.model.entity.Role;
import com.social.media.model.entity.User;
import com.social.media.model.mapper.UserMapper;
import com.social.media.service.RoleService;
import com.social.media.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static com.social.media.controller.StaticHelperForMVC.asJsonString;
import static com.social.media.controller.StaticHelperForMVC.createUser;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTests {
    private static final String BASE_URL = "/api/users";

    private final MockMvc mvc;
    private final UserService userService;
    private final UserMapper mapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private String tokenUser;

    @Autowired
    public UserControllerTests(MockMvc mvc, UserService userService, UserMapper userMapper,
                               RoleService roleService, PasswordEncoder passwordEncoder) {
        this.mvc = mvc;
        this.userService = userService;
        this.mapper = userMapper;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setUp() throws Exception {
        tokenAdmin = mvc.perform(post("/api/auth/login/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithUsername("skallet24", "1111"))
                )
        ).andReturn().getResponse().getContentAsString();

        tokenUser = mvc.perform(post("/api/auth/login/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithEmail("garry@mail.co", "2222"))
                )
        ).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void test_InjectedComponents() {
        assertThat(mvc).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(passwordEncoder).isNotNull();
    }

    @Test
    public void test_Valid_GetAllUsers_AdminAuthorization() throws Exception {
        List<UserResponse> expected = userService.getAll()
                .stream()
                .map(mapper::createUserResponseFromUser)
                .toList();

        mvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "List of users, must be equal!")
                );
    }

    @Test
    public void test_Invalid_GetAllUsers_UserAuthorization() throws Exception {
        mvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetAllUsersByFirstName_AdminAuthorization() throws Exception {
        String firstName = "Garry";

        List<UserResponse> expected = userService.getAllByFirstName(firstName)
                .stream()
                .map(mapper::createUserResponseFromUser)
                .toList();

        mvc.perform(get(BASE_URL + "/first-name/{first-name}", firstName)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "List of users that reads by first name, must be equal.")
                );
    }

    @Test
    public void test_Invalid_GetAllUsersByFirstName_UserAuthorization() throws Exception {
        mvc.perform(get(BASE_URL + "/first-name/{first-name}", "Garry")
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetAllUsersByLastName_AdminAuthorization() throws Exception {
        String lastName = "Jones";

        List<UserResponse> expected = userService.getAllByLastName(lastName)
                .stream()
                .map(mapper::createUserResponseFromUser)
                .toList();

        mvc.perform(get(BASE_URL + "/last-name/{last-name}", lastName)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "List of users that reads by last name, must be equal.")
                );
    }

    @Test
    public void test_Invalid_GetAllUsersByLastName_UserAuthorization() throws Exception {
        mvc.perform(get(BASE_URL + "/first-name/{first-name}", "Jones")
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetAllUsersByRole_AdminAuthorization() throws Exception {
        String role = "user";

        List<UserResponse> expected = userService.getAllByRole(role)
                .stream()
                .map(mapper::createUserResponseFromUser)
                .toList();

        mvc.perform(get(BASE_URL + "/role/{role}", role)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "List of users that reads by role name, must be equal.")
                );
    }

    @Test
    public void test_Invalid_GetAllUsersByRole_UserAuthorization() throws Exception {
        mvc.perform(get(BASE_URL + "/role/{role}", "ADMIN")
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(
                                result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetUserById_AdminAuthorization() throws Exception {
        long userId = 3L;
        testGetUserById(userId, tokenAdmin);
    }

    @Test
    public void test_Valid_GetUserById_UserAuthorization() throws Exception {
        long userId = 2L;
        testGetUserById(userId, tokenUser);
    }

    private void testGetUserById(long userId, String token) throws Exception {
        UserResponse expected = mapper.createUserResponseFromUser(userService.readById(userId));

        mvc.perform(get(BASE_URL + "/{id}", userId)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Users read by same id, so they must be equal.")
                );
    }

    @Test
    public void test_Invalid_GetUserById_UserAuthorization() throws Exception {
        long userId = 1L;

        mvc.perform(get(BASE_URL + "/{id}", userId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @ParameterizedTest
    @MethodSource("argumentsForGetUserByUsernameOrEmail_Valid")
    public void test_Valid_GetUserByUsernameOrEmail_AdminAuthorization(String usernameOrEmail) throws Exception {
        checkGetUserByUsernameOrEmail(usernameOrEmail, tokenAdmin);
    }

    @ParameterizedTest
    @MethodSource("argumentsForGetUserByUsernameOrEmail_Valid")
    public void test_Valid_GetUserByUsernameOrEmail_UserAuthorization(String usernameOrEmail) throws Exception {
        checkGetUserByUsernameOrEmail(usernameOrEmail, tokenUser);
    }

    private void checkGetUserByUsernameOrEmail(String usernameOrEmail, String token) throws Exception {
        UserResponse expected = mapper.createUserResponseFromUser(userService.getUserByUsernameOrEmail(usernameOrEmail));

        mvc.perform(get(BASE_URL + "/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Users that read by username or email must be equal.")
                );
    }

    @ParameterizedTest
    @MethodSource("argumentsForGetUserByUsernameOrEmail_Invalid")
    public void test_Invalid_GetUserByUsernameOrEmail_UserAuthorization(String usernameOrEmail) throws Exception {
        mvc.perform(get(BASE_URL + "/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    private static Stream<String> argumentsForGetUserByUsernameOrEmail_Valid() {
        return Stream.of("garry.potter", "garry@mail.co");
    }

    private static Stream<String> argumentsForGetUserByUsernameOrEmail_Invalid() {
        return Stream.of("oil", "olivia@mail.co");
    }

    @Test
    public void test_Valid_CreateAdmin_AdminAuthorization() throws Exception {
        String username = "new";
        String firstName = "First";
        String lastName = "Last";
        String email = "mail@mail.co";
        String password = "2345";

        UserCreateRequest userCreateRequest = createUser(username, firstName, lastName, email, password);
        User user = mapper.createUserFromUserCreateRequest(userCreateRequest);
        user.setRole(roleService.readByName("ADMIN"));

        UserResponse expected = mapper.createUserResponseFromUser(user);

        mvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userCreateRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals(asJsonString(expected).substring(8),
                                result.getResponse().getContentAsString().substring(9),
                                "This test must be equal, substring for not same id`s, because we create user in this url, so he has not 0 id.")
                );
    }

    @Test
    public void test_Invalid_CreateAdmin_UserAuthorization() throws Exception {
        mvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(createUser("new", "First", "Last", "mail@mail.co", "09875"))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_CreateUserWithCustomRole_AdminAuthorization() throws Exception {
        Role role = roleService.create("NEW");
        String username = "new.user";
        String firstName = "First";
        String lastName = "Last";
        String email = "mail23@mail.co";
        String password = "2345";
        String roleName = role.getName();

        UserCreateRequestWithRole userCreateRequestWithRole = new UserCreateRequestWithRole(username, firstName, lastName, email, password, roleName);
        User user = mapper.createUserFromUserCreateRequestWithRole(userCreateRequestWithRole);
        user.setRole(role);

        UserResponse expected = mapper.createUserResponseFromUser(user);

        mvc.perform(post(BASE_URL + "/custom-role")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userCreateRequestWithRole))
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals(asJsonString(expected).substring(8),
                                result.getResponse().getContentAsString().substring(9),
                                "This test must be equal, substring for not same id`s, because we create user in this url, so he has not 0 id.")
                );
    }

    @Test
    public void test_Invalid_CreateUserWithCustomRole_UserAuthorization() throws Exception {
        mvc.perform(post(BASE_URL + "/custom-role")
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserCreateRequestWithRole(
                                        "new", "First", "Last", "mail@mail.co", "09875", "NEW"
                                ))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_UpdateFullUserById_AdminAuthorization() throws Exception {
        long userId = 3L;
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readById(userId));
        String oldPassword = "3333";
        String newLastName = "Newlastname";

        mvc.perform(put(BASE_URL + "/{id}", userId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(userId, unexpected.getUsername(), unexpected.getEmail(), unexpected.getFirstName(),
                                                newLastName, oldPassword, "new password")
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString()).contains(String.format("\"last_name\":\"%s\"", newLastName))
                );

        assertFalse(passwordEncoder.matches(oldPassword, userService.readById(userId).getPassword()));
        assertThat(unexpected.getLastName()).isNotEqualTo(newLastName);
    }

    @Test
    public void test_Valid_UpdateFullUserById_UserAuthorization() throws Exception {
        long userId = 2L;
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readById(userId));
        String oldPassword = "2222";
        String newFirstName = "Sergio";

        mvc.perform(put(BASE_URL + "/{id}", userId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(userId, unexpected.getUsername(), unexpected.getEmail(), newFirstName,
                                                unexpected.getLastName(), oldPassword, "098765432110")
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString()).contains(String.format("\"first_name\":\"%s\"", newFirstName))
                );

        assertFalse(passwordEncoder.matches(oldPassword, userService.readById(userId).getPassword()));
        assertThat(unexpected.getFirstName()).isNotEqualTo(newFirstName);
    }

    @Test
    public void test_Invalid_UpdateFullUserById_UserAuthorization_AuthUserDoesNotMatchUpdated() throws Exception {
        long userId = 1L;
        String password = "1111";

        mvc.perform(put(BASE_URL + "/{id}", userId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(userId, "username", "newemail@mail.co", "Sergio",
                                                "Last", password, "098765432110")
                                )
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertTrue(passwordEncoder.matches(password, userService.readById(userId).getPassword()));
    }

    @Test
    public void test_Invalid_UpdateFullUserById_AdminAuthorization_UpdateUserDoesNotMatchSelected() throws Exception {
        long validUserId = 1L;
        String password = "1111";

        mvc.perform(put(BASE_URL + "/{id}", validUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(validUserId - new Random(3).nextLong(),
                                                "username", "newemail@mail.co", "Sergio",
                                                "Last", password, "098765432110")
                                )
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertTrue(passwordEncoder.matches(password, userService.readById(validUserId).getPassword()));
    }

    @Test
    public void test_Invalid_UpdateFullUserById_WrongPassword() throws Exception {
        long validUserId = 1L;
        String password = "2222";

        mvc.perform(put(BASE_URL + "/{id}", validUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(validUserId,
                                                "username", "newemail@mail.co", "Sergio",
                                                "Last", password, "098765432110")
                                )
                        )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Wrong old password")
                );

        assertFalse(passwordEncoder.matches(password, userService.readById(validUserId).getPassword()));
    }

    @Test
    public void test_Valid_UpdateFullUserByUsername_AdminAuthorization() throws Exception {
        String username = "oil";

        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readByUsername(username));
        String oldPass = "3333";
        String newLastName = "Pipe";

        mvc.perform(put(BASE_URL + "/username/{username}", username)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateRequest(unexpected.getId(), unexpected.getUsername(), unexpected.getEmail(), unexpected.getFirstName(),
                                        newLastName, oldPass, "pipes2509!kogk"))
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString()).contains(String.format("\"last_name\":\"%s\"", newLastName))
                );

        assertFalse(passwordEncoder.matches(oldPass, userService.readByUsername(username).getPassword()));
        assertThat(unexpected.getLastName()).isNotEqualTo(newLastName);
    }

    @Test
    public void test_Valid_UpdateFullUserByUsername_UserAuthorization() throws Exception {
        String username = "garry.potter";
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readByUsername(username));

        String oldPass = "2222";
        String newFirstName = "Pipe";

        mvc.perform(put(BASE_URL + "/username/{username}", username)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateRequest(unexpected.getId(), unexpected.getUsername(), unexpected.getEmail(), newFirstName,
                                        unexpected.getLastName(), oldPass, "forupdatepass174892"))
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString()).contains(String.format("\"first_name\":\"%s\"", newFirstName))

                );

        assertFalse(passwordEncoder.matches(oldPass, userService.readByUsername(username).getPassword()));
        assertThat(unexpected.getFirstName()).isNotEqualTo(newFirstName);
    }

    @Test
    public void test_Invalid_UpdateFullUserByUsername_UserAuthorization_AuthUserDoesNotMatchUpdated() throws Exception {
        String username = "skallet24";
        User user = userService.readByUsername(username);
        String password = "1111";

        mvc.perform(put(BASE_URL + "/username/{username}", username)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateRequest(user.getId(), username, user.getEmail(), "New",
                                        "New", password, "1234"))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertTrue(passwordEncoder.matches(password, userService.readByUsername(username).getPassword()));
    }

    @Test
    public void test_Invalid_UpdateFullUserByUsername_AdminAuthorization_UpdateUserDoesNotMatchSelected() throws Exception {
        String validUsername = "oil";
        User user = userService.readByUsername(validUsername);
        String password = "3333";

        mvc.perform(put(BASE_URL + "/username/{username}", validUsername)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(user.getId(), "skallet24", "newemail@mail.co", "Sergio",
                                                "Last", password, "098765432110")
                                )
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertTrue(passwordEncoder.matches(password, userService.readByUsername(validUsername).getPassword()));
    }

    @Test
    public void test_Invalid_UpdateFullUserByUsername_WrongPassword() throws Exception {
        String username = "skallet24";
        String password = "2222";

        mvc.perform(put(BASE_URL + "/username/{username}", username)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(1L,
                                                username, "newemail@mail.co", "Sergio",
                                                "Last", password, "098765432110")
                                )
                        )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Wrong old password")
                );

        assertFalse(passwordEncoder.matches(password, userService.readByUsername(username).getPassword()));
    }

    @Test
    public void test_Valid_UpdateFullUserByEmail_AdminAuthorization() throws Exception {
        String email = "olivia@mail.co";
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readByEmail(email));
        String oldPassword = "3333";
        String newLastName = "Lolita";

        mvc.perform(put(BASE_URL + "/email/{email}", email)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateRequest(unexpected.getId(), unexpected.getUsername(), unexpected.getEmail(),
                                        unexpected.getFirstName(), newLastName, oldPassword, "new oil pass for test22341"))
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString()).contains(String.format("\"last_name\":\"%s\"", newLastName))
                );

        assertFalse(passwordEncoder.matches(oldPassword, userService.readByEmail(email).getPassword()));
        assertThat(unexpected.getLastName()).isNotEqualTo(newLastName);
    }

    @Test
    public void test_Valid_UpdateFullUserByEmail_UserAuthorization() throws Exception {
        String email = "garry@mail.co";
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readByEmail(email));
        String oldPassword = "2222";
        String newFirstName = "Thomas";

        mvc.perform(put(BASE_URL + "/email/{email}", email)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateRequest(unexpected.getId(), unexpected.getUsername(), unexpected.getEmail(),
                                        newFirstName, unexpected.getLastName(), oldPassword, "thomas password"))
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString()).contains(String.format("\"first_name\":\"%s\"", newFirstName))
                );

        assertFalse(passwordEncoder.matches(oldPassword, userService.readByEmail(email).getPassword()));
        assertThat(unexpected.getFirstName()).isNotEqualTo(newFirstName);
    }

    @Test
    public void test_Invalid_UpdateFullUserByEmail_UserAuthorization_AuthUserDoesNotMatchUpdated() throws Exception {
        String email = "jone@mail.co";
        User user = userService.readByEmail(email);
        String password = "1111";

        mvc.perform(put(BASE_URL + "/email/{email}", email)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateRequest(user.getId(), user.getUsername(), email, "New",
                                        "New", password, "1234"))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertTrue(passwordEncoder.matches(password, userService.readByEmail(email).getPassword()));
    }

    @Test
    public void test_Invalid_UpdateFullUserByEmail_AdminAuthorization_UpdateUserDoesNotMatchSelected() throws Exception {
        String validEmail = "olivia@mail.co";
        User user = userService.readByEmail(validEmail);
        String password = "3333";

        mvc.perform(put(BASE_URL + "/email/{email}", validEmail)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(user.getId(), "username", "garry@mail.co", "Sergio",
                                                "Last", password, "098765432110")
                                )
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertTrue(passwordEncoder.matches(password, userService.readByEmail(validEmail).getPassword()));
    }

    @Test
    public void test_Invalid_UpdateFullUserByEmail_WrongPassword() throws Exception {
        String email = "jone@mail.co";
        String password = "2222";
        User user = userService.readByEmail(email);

        mvc.perform(put(BASE_URL + "/email/{email}", email)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdateRequest(user.getId(),
                                                "username", email, "Sergio",
                                                "Last", password, "098765432110")
                                )
                        )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Wrong old password")
                );

        assertFalse(passwordEncoder.matches(password, user.getPassword()));
    }

    @Test
    public void test_Valid_UpdateUserNamesById_AdminAuthorization() throws Exception {
        long userId = 3L;
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readById(userId));

        String newFirstName = "Newfirst";
        String newLastName = "Newlast";

        String oldPassword = "3333";

        mvc.perform(put(BASE_URL + "/name/{id}", userId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateNamesRequest(newFirstName, newLastName))
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString())
                                        .contains(String.format("\"first_name\":\"%s\"", newFirstName))
                                        .contains(String.format("\"last_name\":\"%s\"", newLastName))
                );

        User user = userService.readById(userId);

        assertTrue(passwordEncoder.matches(oldPassword, user.getPassword()));
        assertThat(unexpected.getFirstName()).isNotEqualTo(user.getFirstName());
        assertThat(unexpected.getLastName()).isNotEqualTo(user.getLastName());
    }

    @Test
    public void test_Valid_UpdateUserNamesById_UserAuthorization() throws Exception {
        long userId = 2L;
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readById(userId));

        String newFirstName = "Garrynew";
        String newLastName = "Garrynew";

        String oldPassword = "2222";

        mvc.perform(put(BASE_URL + "/name/{id}", userId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateNamesRequest(newFirstName, newLastName))
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString())
                                        .contains(String.format("\"first_name\":\"%s\"", newFirstName))
                                        .contains(String.format("\"last_name\":\"%s\"", newLastName))
                );

        User user = userService.readById(userId);

        assertTrue(passwordEncoder.matches(oldPassword, user.getPassword()));
        assertThat(unexpected.getFirstName()).isNotEqualTo(user.getFirstName());
        assertThat(unexpected.getLastName()).isNotEqualTo(user.getLastName());
    }

    @Test
    public void test_Invalid_UpdateUserNamesById_UserAuthorization() throws Exception {
        long userId = 1L;
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readById(userId));

        String newFirstName = "First";
        String newLastName = "Last";

        String oldPassword = "1111";

        mvc.perform(put(BASE_URL + "/name/{id}", userId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateNamesRequest(newFirstName, newLastName))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        User user = userService.readById(userId);

        assertTrue(passwordEncoder.matches(oldPassword, user.getPassword()));
        assertThat(unexpected.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(unexpected.getLastName()).isEqualTo(user.getLastName());
    }

    @ParameterizedTest
    @MethodSource("argumentsOfGarryUsernameAndEmail")
    public void test_Valid_UpdateUserNamesByUsername_AdminAuthorization(String usernameOrEmail) throws Exception {
        testValidUpdateUserNamesByUsername(usernameOrEmail, tokenAdmin);
    }

    @ParameterizedTest
    @MethodSource("argumentsOfGarryUsernameAndEmail")
    public void test_Valid_UpdateUserNamesByUsername_UserAuthorization(String usernameOrEmail) throws Exception {
        testValidUpdateUserNamesByUsername(usernameOrEmail, tokenUser);
    }

    private void testValidUpdateUserNamesByUsername(String usernameOrEmail, String token) throws Exception {
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.getUserByUsernameOrEmail(usernameOrEmail));

        String newFirstName = "Patric";
        String newLastName = "Last";

        String oldPassword = "2222";

        mvc.perform(put(BASE_URL + "/name/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateNamesRequest(newFirstName, newLastName))
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "User must not be equals, because 'unexpected' it`s old version of this model of user."),
                        result ->
                                assertThat(result.getResponse().getContentAsString())
                                        .contains(String.format("\"first_name\":\"%s\"", newFirstName))
                                        .contains(String.format("\"last_name\":\"%s\"", newLastName))
                );

        User user = userService.getUserByUsernameOrEmail(usernameOrEmail);

        assertTrue(passwordEncoder.matches(oldPassword, user.getPassword()));
        assertThat(unexpected.getFirstName()).isNotEqualTo(user.getFirstName());
        assertThat(unexpected.getLastName()).isNotEqualTo(user.getLastName());
    }

    private static Stream<String> argumentsOfGarryUsernameAndEmail() {
        return Stream.of("garry.potter", "garry@mail.co");
    }

    @ParameterizedTest
    @MethodSource("argumentsOfOliviaUsernameOrEmail")
    public void test_Invalid_UpdateUserNamesByUsername_UserAuthorization(String usernameOrEmail) throws Exception {
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.getUserByUsernameOrEmail(usernameOrEmail));

        String newFirstName = "First";
        String newLastName = "Last";

        String oldPassword = "3333";

        mvc.perform(put(BASE_URL + "/name/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdateNamesRequest(newFirstName, newLastName))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        User user = userService.getUserByUsernameOrEmail(usernameOrEmail);

        assertTrue(passwordEncoder.matches(oldPassword, user.getPassword()));
        assertThat(unexpected.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(unexpected.getLastName()).isEqualTo(user.getLastName());
    }

    private static Stream<String> argumentsOfOliviaUsernameOrEmail() {
        return Stream.of("oil", "olivia@mail.co");
    }

    @Test
    public void test_Valid_UpdateUserPasswordById_UserAuthorization() throws Exception {
        long userId = 2L;
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.readById(userId));
        String oldPass = "2222";

        mvc.perform(put(BASE_URL + "/password/{id}", userId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdatePasswordRequest(oldPass, "new pass for update"))
                        )
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                "User must not be equals, because 'unexpected' it`s old version of this model of user.")
                );

        assertFalse(passwordEncoder.matches(oldPass, userService.readById(userId).getPassword()));
    }

    @Test
    public void test_Invalid_UpdateUserPasswordById_AdminAuthorization() throws Exception {
        long userId = 3L;
        String oldPass = "3333";

        mvc.perform(put(BASE_URL + "/password/{id}", userId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdatePasswordRequest(oldPass, "new pass for update"))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertTrue(passwordEncoder.matches(oldPass, userService.readById(userId).getPassword()));
    }

    @Test
    public void test_Invalid_UpdateUserPasswordById_WrongPassword() throws Exception {
        long userID = 1L;
        String password = "2222";

        mvc.perform(put(BASE_URL + "/password/{id}", userID)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                       new UserUpdatePasswordRequest(password, "newpass")
                                )
                        )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Wrong old password")
                );

        assertFalse(passwordEncoder.matches(password, userService.readById(userID).getPassword()));
    }

    @ParameterizedTest
    @MethodSource("argumentsOfGarryUsernameAndEmail")
    public void test_Valid_UpdateUserPasswordByUsername_UserAuthorization(String usernameOrEmail) throws Exception {
        UserResponse unexpected = mapper.createUserResponseFromUser(userService.getUserByUsernameOrEmail(usernameOrEmail));
        String oldPass = "2222";

        mvc.perform(put(BASE_URL + "/password/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdatePasswordRequest(oldPass, "updated pass"))
                        )
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                "User must not be equals, because 'unexpected' it`s old version of this model of user.")
                );

        assertFalse(passwordEncoder.matches(oldPass, userService.getUserByUsernameOrEmail(usernameOrEmail).getPassword()));
    }

    @ParameterizedTest
    @MethodSource("argumentsOfGarryUsernameAndEmail")
    public void test_Invalid_UpdateUserPasswordByUsername_AdminAuthorization(String usernameOrEmail) throws Exception {
        String oldPass = "2222";

        mvc.perform(put(BASE_URL + "/password/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new UserUpdatePasswordRequest(oldPass, "new pass for update"))
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertTrue(passwordEncoder.matches(oldPass, userService.getUserByUsernameOrEmail(usernameOrEmail).getPassword()));
    }

    @ParameterizedTest
    @MethodSource("argumentsOfGarryUsernameAndEmail")
    public void test_Invalid_UpdateUserPasswordByUsername_WrongPassword(String usernameOrEmail) throws Exception {
        String password = "3333";

        mvc.perform(put(BASE_URL + "/password/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        new UserUpdatePasswordRequest(password, "newpass")
                                )
                        )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Wrong old password")
                );

        assertFalse(passwordEncoder.matches(password, userService.getUserByUsernameOrEmail(usernameOrEmail).getPassword()));
    }

    @Test
    public void test_Valid_DeleteUserById_AdminAuthorization() throws Exception {
        long userId = 3L;
        User user = userService.readById(userId);
        List<User> users = userService.getAll();

        mvc.perform(delete(BASE_URL + "/{id}", userId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                "User with name " + user.getName() + " successfully deleted!",
                                "Here must be a message about successful deleting user.")
                );

        assertTrue(userService.getAll().size() < users.size(),
                "After deleting users size that writes to obj before deleting must be bigger!");
    }

    @Test
    public void test_Valid_DeleteUserById_UserAuthorization() throws Exception {
        long userId = 2L;
        User user = userService.readById(userId);
        List<User> users = userService.getAll();

        mvc.perform(delete(BASE_URL + "/{id}", userId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                "User with name " + user.getName() + " successfully deleted!",
                                "Here must be a message about successful deleting user.")
                );

        assertTrue(userService.getAll().size() < users.size(),
                "After deleting users size that writes to obj before deleting must be bigger!");
    }

    @Test
    public void test_Invalid_DeleteUserById_UserAuthorization_AuthUserDoesNotMatchDeleted() throws Exception {
        long userId = 1L;
        List<User> users = userService.getAll();

        mvc.perform(delete(BASE_URL + "/{id}", userId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertEquals(userService.getAll(), users,
                "Users lists must be equals, because we did not delete anyone!");
    }

    @ParameterizedTest
    @MethodSource("argumentsOfGarryUsernameAndEmail")
    public void test_Valid_DeleteUserByUsername_AdminAuthorization(String usernameOrEmail) throws Exception {
        testDeleteUserByUsername(usernameOrEmail, tokenAdmin);
    }

    @ParameterizedTest
    @MethodSource("argumentsOfGarryUsernameAndEmail")
    public void test_Valid_DeleteUserByUsername_UserAuthorization(String usernameOrEmail) throws Exception {
        testDeleteUserByUsername(usernameOrEmail, tokenUser);
    }

    private void testDeleteUserByUsername(String usernameOrEmail, String token) throws Exception {
        User user = userService.getUserByUsernameOrEmail(usernameOrEmail);
        List<User> users = userService.getAll();

        mvc.perform(delete(BASE_URL + "/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                "User with name " + user.getName() + " successfully deleted!",
                                "Here must be a message about successful deleting user.")
                );

        assertTrue(userService.getAll().size() < users.size(),
                "After deleting users size that writes to obj before deleting must be bigger!");
    }

    @ParameterizedTest
    @MethodSource("argumentsOfOliviaUsernameOrEmail")
    public void test_Invalid_DeleteUserByUsername_UserAuthorization_AuthUserDoesNotMatchDeleted(String usernameOrEmail) throws Exception {
        List<User> users = userService.getAll();

        mvc.perform(delete(BASE_URL + "/username-email/{username-or-email}", usernameOrEmail)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );

        assertEquals(userService.getAll(), users,
                "Users lists must be equals, because we did not delete anyone!");
    }
}