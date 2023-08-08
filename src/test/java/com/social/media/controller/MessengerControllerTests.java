package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.messenger.AllMessengersResponse;
import com.social.media.model.dto.messenger.MessengerResponse;
import com.social.media.model.entity.Messenger;
import com.social.media.model.entity.User;
import com.social.media.model.mapper.MessengerMapper;
import com.social.media.service.MessageService;
import com.social.media.service.MessengerService;
import com.social.media.service.RoleService;
import com.social.media.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.social.media.controller.StaticHelperForMVC.asJsonString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class MessengerControllerTests {
    private static final String BASE_URL = "/api/users/{owner-id}/messengers";

    private final MockMvc mvc;
    private final MessengerService messengerService;
    private final MessageService messageService;
    private final MessengerMapper mapper;
    private final UserService userService;
    private final RoleService roleService;

    private String tokenAdmin;
    private String tokenOlivia;
    private String tokenGarry;

    @Autowired
    public MessengerControllerTests(MockMvc mvc, MessengerService messengerService, MessageService messageService,
                                    MessengerMapper mapper, UserService userService, RoleService roleService) {
        this.mvc = mvc;
        this.messengerService = messengerService;
        this.messageService = messageService;
        this.mapper = mapper;
        this.userService = userService;
        this.roleService = roleService;
    }

    @BeforeEach
    void setUp() throws Exception {
        tokenAdmin = mvc.perform(post("/api/auth/login/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithUsername("skallet24", "1111"))
                )
        ).andReturn().getResponse().getContentAsString();

        tokenGarry = mvc.perform(post("/api/auth/login/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithUsername("garry.potter", "2222"))
                )
        ).andReturn().getResponse().getContentAsString();

        tokenOlivia = mvc.perform(post("/api/auth/login/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithEmail("olivia@mail.co", "3333"))
                )
        ).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(mvc).isNotNull();
        assertThat(messengerService).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(messageService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(roleService).isNotNull();
    }

    @Test
    public void test_Valid_GetAllMessengers_UserAuth() throws Exception {
        long ownerId = 3L;

        List<AllMessengersResponse> expected = messengerService.getAllByOwnerId(ownerId)
                .stream()
                .map(messenger -> mapper.createAllMessengersResponseFromMessenger(messenger, messageService.getLastMessage(messenger.getId())))
                .toList();

        mvc.perform(get(BASE_URL, ownerId)
                        .header("Authorization", "Bearer " + tokenOlivia)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Getting all messengers must be the same.")
                );
    }

    @Test
    public void test_Invalid_GetAllMessengers_AdminAuth_AuthUserAndOwnerIsNotSame() throws Exception {
        long ownerId = 2L;

        mvc.perform(get(BASE_URL, ownerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetMessengerById_UserAuth() throws Exception {
        long ownerId = 2L;
        long messengerId = 4L;

        MessengerResponse expected = mapper.createMessengerResponseFromMessenger(messengerService.readById(messengerId),
                messageService.getAllByMessenger(messengerId));

        mvc.perform(get(BASE_URL + "/{id}", ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenGarry)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString())
                );
    }

    @Test
    public void test_Invalid_GetMessengerById_AdminAuth_UsersIsNotSame() throws Exception {
        long ownerId = 3L;
        long messengerId = 6L;

        mvc.perform(get(BASE_URL + "/{id}", ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_GetMessengerById_UserAuth_UserNotOwnerOfMessenger() throws Exception {
        long ownerId = 3L;
        long messengerId = 1L;

        mvc.perform(get(BASE_URL + "/{id}", ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenOlivia)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_CreateMessenger_UserAuth() throws Exception {
        long ownerId = 2L;
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("newpass");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@mail.co");

        User recipient = userService.create(user, roleService.readByName("USER"));

        mvc.perform(post(BASE_URL, ownerId)
                        .header("Authorization", "Bearer " + tokenGarry)
                        .param("username", recipient.getUsername())
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains("\"recipient_id\":" + recipient.getId())
                );
    }

    @Test
    public void test_Invalid_CreateMessenger_AdminAuth_UserAuthAndOwnerIsNotSame() throws Exception {
        long ownerId = 3L;

        User user = new User();
        user.setUsername("userforinvalidtest");
        user.setPassword("newpass");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("muser@mail.co");

        User recipient = userService.create(user, roleService.readByName("USER"));

        mvc.perform(post(BASE_URL, ownerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("username", recipient.getUsername())
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_CreateMessenger_UserAuth_UsersToCreateSame() throws Exception {
        long ownerId = 3L;

        mvc.perform(post(BASE_URL, ownerId)
                        .header("Authorization", "Bearer " + tokenOlivia)
                        .param("username", userService.readById(ownerId).getUsername())
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("You cannot create messenger for you, so write please, another user!")
                );
    }

    @Test
    public void test_Valid_DeleteMessenger_AdminAuth() throws Exception {
        long ownerId = 1L;
        long messengerId = 2L;
        Messenger messenger = messengerService.readById(messengerId);
        List<Messenger> beforeDeleting = messengerService.getAllByOwnerId(ownerId);

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                String.format("Messenger between %s and %s successfully deleted!",
                                        messenger.getOwner().getName(), messenger.getRecipient().getName()),
                                "After deleting user must get this message!")
                );

        assertTrue(beforeDeleting.size() > messengerService.getAllByOwnerId(ownerId).size(),
                "Size before deleting must be bigger than after!");
    }

    @Test
    public void test_Invalid_DeleteMessenger_AdminAuth_UsersIsNotSame() throws Exception {
        long ownerId = 3L;
        long messengerId = 6L;

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_DeleteMessenger_UserAuth_UserNotOwnerOfMessenger() throws Exception {
        long ownerId = 3L;
        long messengerId = 5L;

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenOlivia)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }
}
