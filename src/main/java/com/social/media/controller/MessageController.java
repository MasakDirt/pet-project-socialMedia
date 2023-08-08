package com.social.media.controller;

import com.social.media.model.dto.message.MessageResponse;
import com.social.media.model.mapper.MessageMapper;
import com.social.media.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.social.media.controller.ControllerHelper.getRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/messengers/{messenger-id}/messages")
public class MessageController {
    private final MessageService messageService;
    private final MessageMapper mapper;

    @GetMapping
    @PreAuthorize("@authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(#ownerId, authentication.principal, #messengerId)")
    public List<MessageResponse> getAllMessages(@PathVariable("owner-id") long ownerId, @PathVariable("messenger-id") long messengerId,
                                                Authentication authentication) {
        var responses = messageService.getAllByMessenger(messengerId)
                .stream()
                .map(mapper::createMessageResponseFromMessage)
                .toList();
        log.info("=== GET-USERS-ID-MESSENGERS-ID-MESSAGES === {} - {}",
                getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authMessageService.isAuthAndUserSameAndUserOwnerOfMessengerAndMessengerContainsMessageWithoutAdmin(#ownerId, authentication.principal, #messengerId, #id)")
    public MessageResponse getMessage(@PathVariable("owner-id") long ownerId, @PathVariable("messenger-id") long messengerId,
                                      @PathVariable String id, Authentication authentication) {
        var response = mapper.createMessageResponseFromMessage(messageService.readById(id));
        log.info("=== GET-USERS-ID-MESSENGERS-ID-MESSAGE-ID === {} - {}",
                getRole(authentication), authentication.getPrincipal());

        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(#ownerId, authentication.principal, #messengerId)")
    public List<MessageResponse> createMessage(@PathVariable("owner-id") long ownerId, @PathVariable("messenger-id") long messengerId,
                                               @RequestParam String message, Authentication authentication) {
        messageService.create(messengerId, ownerId, message);
        var responses = messageService.getAllByMessenger(messengerId)
                .stream()
                .map(mapper::createMessageResponseFromMessage)
                .toList();
        log.info("=== POST-USERS-ID-MESSENGERS-ID-MESSAGES === {} - {}",
                getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authMessageService.isAuthAndUserSameAndUserOwnerOfMessengerAndMessengerContainsMessageWithoutAdmin(#ownerId, authentication.principal, #messengerId, #id)")
    public List<MessageResponse> updateMessage(@PathVariable("owner-id") long ownerId, @PathVariable("messenger-id") long messengerId,
                                               @PathVariable String id, @NotEmpty @RequestParam("message") @Valid String updatedMessage,
                                               Authentication authentication) {
        messageService.update(id, updatedMessage);
        var responses = messageService.getAllByMessenger(messengerId)
                .stream()
                .map(mapper::createMessageResponseFromMessage)
                .toList();
        log.info("=== PUT-USERS-ID-MESSENGERS-ID-MESSAGE-ID === {} - {}",
                getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authMessageService.isAuthAndUserSameAndUserOwnerOfMessengerAndMessengerContainsMessageWithoutAdmin(#ownerId, authentication.principal, #messengerId, #id)")
    public ResponseEntity<String> deleteMessage(@PathVariable("owner-id") long ownerId, @PathVariable("messenger-id") long messengerId,
                                        @PathVariable String id, Authentication authentication) {
        messageService.delete(id);
        log.info("=== DELETE-USERS-ID-MESSENGERS-ID-MESSAGE-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("Message for messenger with id: %d successfully deleted!", messengerId));
    }
}
