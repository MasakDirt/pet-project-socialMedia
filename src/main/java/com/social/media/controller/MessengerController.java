package com.social.media.controller;

import com.social.media.model.dto.messenger.AllMessengersResponse;
import com.social.media.model.dto.messenger.MessengerResponse;
import com.social.media.model.mapper.MessengerMapper;
import com.social.media.service.MessageService;
import com.social.media.service.MessengerService;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.social.media.controller.ControllerHelper.getRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/messengers")
public class MessengerController {
    private final MessengerService messengerService;
    private final MessengerMapper mapper;
    private final MessageService messageService;

    @GetMapping
    @PreAuthorize("@authUserService.isAuthAndUserSameWithoutAdmin(#ownerId, authentication.principal)")
    public List<AllMessengersResponse> getAllMessengers(@PathVariable("owner-id") long ownerId, Authentication authentication) {
        var responses = messengerService.getAllByOwnerId(ownerId)
                .stream()
                .map(messenger -> mapper.createAllMessengersResponseFromMessenger(messenger, messageService.getLastMessage(messenger.getId())))
                .toList();
        log.info("=== GET-USERS-ID-MESSENGERS === {} - {}", getRole(authentication), authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(#ownerId, authentication.principal, #id)")
    public MessengerResponse getMessengerById(@PathVariable("owner-id") long ownerId, @PathVariable long id, Authentication authentication) {
        var response = mapper.createMessengerResponseFromMessenger(messengerService.readById(id), messageService.getAllByMessenger(id));
        log.info("=== GET-USERS-ID-MESSENGER-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return response;
    }

    @PostMapping
    @PreAuthorize("@authUserService.isAuthAndUserSameWithoutAdmin(#ownerId, authentication.principal)")
    public MessengerResponse createNewMessenger(@PathVariable("owner-id") long ownerId,
                                                @RequestParam("username") @NotEmpty String recipientUsername, Authentication authentication) {
        var created = messengerService.create(ownerId, recipientUsername);
        var response = mapper.createMessengerResponseFromMessenger(created, messageService.getAllByMessenger(created.getId()));
        log.info("=== POST-USERS-ID-MESSENGERS === {} - {}", getRole(authentication), authentication.getPrincipal());

        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(#ownerId, authentication.principal, #id)")
    public ResponseEntity<String> deleteMessenger(@PathVariable("owner-id") long ownerId, @PathVariable long id, Authentication authentication) {
        var messenger = messengerService.readById(id);
        messengerService.delete(id);
        log.info("=== DELETE-USERS-ID-MESSENGER-ID === {} - {}", getRole(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("Messenger between %s and %s successfully deleted!", messenger.getOwner().getName(), messenger.getRecipient().getName()));
    }
}
