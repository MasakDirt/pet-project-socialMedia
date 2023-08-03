package com.social.media.service.authorization;

import com.social.media.model.entity.Message;
import com.social.media.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthMessageService {
    private final AuthMessengerService authMessengerService;
    private final MessageService messageService;

    public boolean isAuthAndUserSameAndUserOwnerOfMessengerAndMessengerContainsMessageWithoutAdmin(
            long ownerId, String currentUsername, long messengerId, String messageId
    ) {
        return authMessengerService.isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(ownerId, currentUsername, messengerId) &&
                getMessage(messageId).getMessengerId() == messengerId;
    }

    public Message getMessage(String messageId) {
        return messageService.readById(messageId);
    }
}
