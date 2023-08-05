package com.social.media.service.authorization;

import com.social.media.model.entity.User;
import com.social.media.service.UserService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthUserServiceTests {
    private final AuthUserService authUserService;
    private final UserService userService;

    @Autowired
    public AuthUserServiceTests(AuthUserService authUserService, UserService userService) {
        this.authUserService = authUserService;
        this.userService = userService;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(authUserService).isNotNull();
        AssertionsForClassTypes.assertThat(userService).isNotNull();
    }

    @Test
    public void test_isAuthAndUserAndUserRequestSame_True() {
        assertTrue(authUserService.isAuthAndUserAndUserRequestSame(2L, 2L, "garry.potter"),
                "Here must be true, because user id and user id response sames and auth user the same with user id!");
    }

    @Test
    public void test_isAuthAndUserAndUserRequestSame_False() {
        assertFalse(authUserService.isAuthAndUserAndUserRequestSame(2L, 1L, "garry.potter"),
                "Here must be false, because user id and user id response sames is not equal");

        assertFalse(authUserService.isAuthAndUserAndUserRequestSame(2L, 2L, "oil"),
                "Here must be false, because auth user not same with user by id!");
    }

    @Test
    public void test_isAuthAndUserAndUserRequestByUsernameSame_True_User() {
        String usernameUser = "oil";

        assertTrue(authUserService.isAuthAndUserAndUserRequestByUsernameSame(usernameUser, usernameUser, usernameUser),
                "Here must be true because we have sames users.");
    }

    @Test
    public void test_isAuthAndUserAndUserRequestByUsernameSame_True_Admin() {
        String usernameUser = "oil";
        String usernameAdmin = "skallet24";

        assertTrue(authUserService.isAuthAndUserAndUserRequestByUsernameSame(usernameUser, usernameUser, usernameAdmin),
                "Here must be true because auth user is admin.");
    }

    @Test
    public void test_isAuthAndUserAndUserRequestByUsernameSame_False() {
        String username = "oil";
        String usernameInvalid = "garry.potter";

        assertFalse(authUserService.isAuthAndUserAndUserRequestByUsernameSame(username, username, usernameInvalid),
                "Here must be false because auth user and user isn`t same.");
    }

    @Test
    public void test_isAuthAndUserAndUserRequestByEmailSame_True_User() {
        String email = "garry@mail.co";
        String currentUsername = "garry.potter";

        assertTrue(authUserService.isAuthAndUserAndUserRequestByEmailSame(email, email, currentUsername),
                "Here must be true because we have sames users.");
    }

    @Test
    public void test_isAuthAndUserAndUserRequestByEmailSame_True_Admin() {
        String emailUser = "garry@mail.co";
        String usernameAdmin = "skallet24";

        assertTrue(authUserService.isAuthAndUserAndUserRequestByEmailSame(emailUser, emailUser, usernameAdmin),
                "Here must be true because auth user is admin.");
    }

    @Test
    public void test_isAuthAndUserAndUserRequestByEmailSame_False() {
        String email = "garry@mail.co";
        String usernameInvalid = "oil";

        assertFalse(authUserService.isAuthAndUserAndUserRequestByEmailSame(email, email, usernameInvalid),
                "Here must be false because auth user and user isn`t same.");
    }

    @Test
    public void test_isAuthAndUserSame_True() {
        long userID = 2L;
        String username = "garry.potter";

        assertTrue(authUserService.isAuthAndUserSame(userID, username),
                "Here must be true because auth user same by id.");
    }

    @Test
    public void test_isAuthAndUserSame_True_Admin() {
        long userID = 2L;
        String username = "skallet24";

        assertTrue(authUserService.isAuthAndUserSame(userID, username),
                "Here must be true because auth user is admin.");
    }

    @Test
    public void test_isAuthAndUserSame_False() {
        long userID = 2L;
        String usernameInvalid = "oil";

        assertFalse(authUserService.isAuthAndUserSame(userID, usernameInvalid),
                "Here must be false because auth user and user isn`t same by id");
    }

    @ParameterizedTest
    @MethodSource("argumentsForIsAuthAndUserSameByUsernameOrEmail_True")
    public void test_IsAuthAndUserSameByUsernameOrEmail_True(String usernameOrEmail) {
        String username = "garry.potter";

        assertTrue(authUserService.isAuthAndUserSameByUsernameOrEmail(usernameOrEmail, username),
                "Here should be true, because user by equals by username or email!");
    }

    private static Stream<String> argumentsForIsAuthAndUserSameByUsernameOrEmail_True() {
        return Stream.of("garry.potter", "garry@mail.co");
    }

    @Test
    public void test_IsAuthAndUserSameByUsernameOrEmail_True_Admin() {
        String username = "garry.potter";
        String usernameAdmin = "skallet24";

        assertTrue(authUserService.isAuthAndUserSameByUsernameOrEmail(username, usernameAdmin),
                "Here must be true because auth user is admin.");
    }

    @ParameterizedTest
    @MethodSource("argumentsForIsAuthAndUserSameByUsernameOrEmail_False")
    public void test_IsAuthAndUserSameByUsernameOrEmail_False(String usernameOrEmail) {
        String username = "garry.potter";

        assertFalse(authUserService.isAuthAndUserSameByUsernameOrEmail(usernameOrEmail, username),
                "Here must be false because auth user and user isn`t same by username or email");
    }

    private static Stream<String> argumentsForIsAuthAndUserSameByUsernameOrEmail_False() {
        return Stream.of("oil", "olivia@mail.co");
    }

    @ParameterizedTest
    @MethodSource("argumentsForIsAuthAndUserSameByUsernameOrEmail_True")
    public void test_IsUserSameByUsernameOrEmail_True(String usernameOrEmail) {
        String username = "garry.potter";

        assertTrue(authUserService.isUserSameByUsernameOrEmail(usernameOrEmail, username),
                "Here should be true, because user by equals by username or email!");
    }

    @ParameterizedTest
    @MethodSource("argumentsForIsAuthAndUserSameByUsernameOrEmail_False")
    public void test_IsUserSameByUsernameOrEmail_False(String usernameOrEmail) {
        String username = "garry.potter";

        assertFalse(authUserService.isUserSameByUsernameOrEmail(usernameOrEmail, username),
                "Here must be false because auth user and user isn`t same by username or email");
    }

    @Test
    public void test_isAuthAndUserSameWithoutAdmin_True() {
        long userId = 3L;
        String username = "oil";

        assertTrue(authUserService.isAuthAndUserSameWithoutAdmin(userId, username),
                "Here must be true because auth user same by id.");
    }

    @Test
    public void test_isAuthAndUserSameWithoutAdmin_False() {
        long userId = 2L;
        String username = "oil";

        assertFalse(authUserService.isAuthAndUserSameWithoutAdmin(userId, username),
                "Here must be false because auth user isn`t same with user that reads by id.");
    }

    @Test
    public void test_GetUser() {
        String currentUsername = "oil";

        User expected = userService.readByUsername(currentUsername);
        User actual = authUserService.getUser(currentUsername);

        assertEquals(expected, actual,
                "As we has the same username, so users must be the same, too!");
    }
}
