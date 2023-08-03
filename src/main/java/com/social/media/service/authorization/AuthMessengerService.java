package com.social.media.service.authorization;

import com.social.media.model.entity.Messenger;
import com.social.media.service.MessengerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthMessengerService {
    private final AuthUserService authUserService;
    private final MessengerService messengerService;

    public boolean isAuthAndUserSameAndUserOwnerOfMessengerWithoutAdmin(long ownerId, String currentUsername, long messengerId) {
       return authUserService.isAuthAndUserSameWithoutAdmin(ownerId, currentUsername) && getMessenger(messengerId).getOwner().getId() == ownerId;
    }

    public Messenger getMessenger(long messengerId) {
        return messengerService.readById(messengerId);
    }
}
