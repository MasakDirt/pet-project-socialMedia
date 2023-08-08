package com.social.media.controller;

import com.social.media.model.dto.message.MessageResponse;
import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.entity.Message;
import com.social.media.model.mapper.MessageMapper;
import com.social.media.repository.MessageRepository;
import com.social.media.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
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
public class MessageControllerTests {
    private static final String BASE_URL = "/api/users/{owner-id}/messengers/{messenger-id}/messages";

    private final MockMvc mvc;
    private final MessageService messageService;
    private final MessageMapper mapper;
    private final MessageRepository messageRepository;

    private String tokenAdmin;
    private String tokenUser;

    @Autowired
    public MessageControllerTests(MockMvc mvc, MessageService messageService, MessageMapper mapper,
                                  MessageRepository messageRepository) {
        this.mvc = mvc;
        this.messageService = messageService;
        this.mapper = mapper;
        this.messageRepository = messageRepository;
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
    public void test_Injected_Components() {
        assertThat(mvc).isNotNull();
        assertThat(messageService).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(messageRepository).isNotNull();
    }

    @Test
    public void test_Valid_GetAllMessages_AdminAuth() throws Exception {
        long ownerId = 1L;
        long messengerId = 2L;
        List<MessageResponse> expected = messageService.getAllByMessenger(messengerId)
                .stream()
                .map(mapper::createMessageResponseFromMessage)
                .toList();

        mvc.perform(get(BASE_URL, ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "After reading be same id lists must be equal!")
                );
    }

    @Test
    public void test_Invalid_DeleteMessenger_AdminAuth_UsersIsNotSame() throws Exception {
        long ownerId = 3L;
        long messengerId = 6L;

        mvc.perform(get(BASE_URL, ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_GetAllMessages_UserAuth_UserNotOwnerOfMessenger() throws Exception {
        long ownerId = 2L;
        long messengerId = 5L;

        mvc.perform(get(BASE_URL, ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetMessage_UserAuth() throws Exception {
        long ownerId = 2L;
        long messengerId = 4L;
        MessageResponse expected = messageService.getAllByMessenger(messengerId)
                .stream()
                .map(mapper::createMessageResponseFromMessage)
                .findAny()
                .orElse(new MessageResponse());

        String messageId = expected.getId();

        mvc.perform(get(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Messages that read by same id must be the same!")
                );
    }

    @Test
    public void test_Invalid_GetMessage_AdminAuth_MessengerIsNotContainMessage() throws Exception {
        long ownerId = 2L;
        long messengerId = 4L;

        String invalidMessageId = messageRepository.findAll()
                .stream()
                .filter(message -> message.getMessengerId() != messengerId)
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(get(BASE_URL + "/{id}", ownerId, messengerId, invalidMessageId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_GetMessage_AdminAuth_UserNotOwnerOfMessenger() throws Exception {
        long ownerId = 1L;
        long messengerId = 3L;
        String messageId = messageService.getAllByMessenger(messengerId)
                .stream()
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(get(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_GetMessage_UserAuth_UsersIsNotSame() throws Exception {
        long ownerId = 1L;
        long messengerId = 3L;
        String messageId = messageService.getAllByMessenger(messengerId)
                .stream()
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(get(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_CreateMessage_AdminAuth() throws Exception {
        long ownerId = 1L;
        long messengerId = 2L;

        List<Message> beforeCreating = messageService.getAllByMessenger(messengerId);
        String expectedMessage = "Hello, what`s up?)";

        mvc.perform(post(BASE_URL, ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("message", expectedMessage)
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull()
                                .contains(expectedMessage)
                );

        assertTrue(beforeCreating.size() < messageService.getAllByMessenger(messengerId).size(),
                "Lists of all message before creating must be smaller than after!");
    }

    @Test
    public void test_Invalid_CreateMessage_UserAuth_UsersIsNotSame() throws Exception {
        long ownerId = 3L;
        long messengerId = 6L;

        mvc.perform(post(BASE_URL, ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("message", "Hello")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_CreateMessage_AdminAuth_UserNotOwnerOfMessenger() throws Exception {
        long ownerId = 1L;
        long messengerId = 3L;

        mvc.perform(post(BASE_URL, ownerId, messengerId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("message", "Hello")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_UpdateMessage_UserAuth() throws Exception {
        long ownerId = 2L;
        long messengerId = 4L;
        MessageResponse unexpected = messageService.getAllByMessenger(messengerId)
                .stream()
                .map(mapper::createMessageResponseFromMessage)
                .findAny()
                .orElse(new MessageResponse());
        String messageId = unexpected.getId();

        String updatedMessage = "new message";

        mvc.perform(put(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("message", updatedMessage)
                )
                .andExpect(status().isOk())
                .andExpectAll(result ->
                                assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                        .contains(updatedMessage),
                        result ->
                                assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                        "After updating messages must not be equals.")
                );

        assertEquals(updatedMessage, messageService.readById(messageId).getMessage(),
                "After updating message that read now must be equal with updated message.");
    }

    @Test
    public void test_Invalid_UpdateMessage_UserAuth_MessengerIsNotContainMessage() throws Exception {
        long ownerId = 2L;
        long messengerId = 1L;

        String invalidMessageId = messageRepository.findAll()
                .stream()
                .filter(message -> message.getMessengerId() != messengerId)
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(put(BASE_URL + "/{id}", ownerId, messengerId, invalidMessageId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("message", "Messenger is not contain message")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_UpdateMessage_AdminAuth_UserNotOwnerOfMessenger() throws Exception {
        long ownerId = 1L;
        long messengerId = 5L;
        String messageId = messageService.getAllByMessenger(messengerId)
                .stream()
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(put(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("message", "User not owner of messenger.")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_UpdateMessage_UserAuth_UsersIsNotSame() throws Exception {
        long ownerId = 3L;
        long messengerId = 6L;
        String messageId = messageService.getAllByMessenger(messengerId)
                .stream()
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(put(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("message", "Users is not same.")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_DeleteMessage_AdminAuth() throws Exception {
        long ownerId = 1L;
        long messengerId = 2L;

        List<Message> beforeDeleting = messageService.getAllByMessenger(messengerId)
                .stream()
                .toList();

        Message deleted = beforeDeleting
                .stream()
                .filter(message -> message.getMessengerId() == messengerId)
                .findAny()
                .orElse(new Message());

        String messageId = deleted.getId();

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(String.format("Message for messenger with id: %d successfully deleted!", messengerId),
                                result.getResponse().getContentAsString(),
                                "After deleting user must get this message!")
                );

        assertTrue(beforeDeleting.size() > messageService.getAllByMessenger(messengerId).size(),
                "After deleting messages that read before must be bigger.");

        assertThrows(EntityNotFoundException.class, () -> messageService.readById(messageId),
                "After deleting we must get EntityNotFoundException.");
    }

    @Test
    public void test_Invalid_DeleteMessage_UserAuth_MessengerIsNotContainMessage() throws Exception {
        long ownerId = 2L;
        long messengerId = 6L;

        String invalidMessageId = messageRepository.findAll()
                .stream()
                .filter(message -> message.getMessengerId() != messengerId)
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, messengerId, invalidMessageId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_DeleteMessage_AdminAuth_UserNotOwnerOfMessenger() throws Exception {
        long ownerId = 1L;
        long messengerId = 3L;
        String messageId = messageService.getAllByMessenger(messengerId)
                .stream()
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_DeleteMessage_UserAuth_UsersIsNotSame() throws Exception {
        long ownerId = 1L;
        long messengerId = 2L;
        String messageId = messageService.getAllByMessenger(messengerId)
                .stream()
                .findAny()
                .orElse(new Message())
                .getId();

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, messengerId, messageId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                                .contains("Access Denied")
                );
    }
}
