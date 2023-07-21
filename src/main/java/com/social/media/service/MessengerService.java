package com.social.media.service;

import com.social.media.model.entity.Messenger;
import com.social.media.repository.MessengerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessengerService {
    private final MessengerRepository messengerRepository;
    private final UserService userService;

    public Messenger create(long ownerId, long recipientId) {
        var messengerForOwner = new Messenger();
        messengerForOwner.setOwner(userService.readById(ownerId));
        messengerForOwner.setRecipient(userService.readById(recipientId));

        var messengerForRecipient = new Messenger();
        messengerForRecipient.setRecipient(userService.readById(ownerId));
        messengerForRecipient.setOwner(userService.readById(recipientId));

        messengerRepository.save(messengerForRecipient);
        return messengerRepository.save(messengerForOwner);
    }

    public Messenger create(long ownerId, String recipientUsername) {
        var messengerForOwner = new Messenger();
        messengerForOwner.setOwner(userService.readById(ownerId));
        messengerForOwner.setRecipient(userService.readByUsername(recipientUsername));

        var messengerForRecipient = new Messenger();
        messengerForRecipient.setRecipient(userService.readById(ownerId));
        messengerForRecipient.setOwner(userService.readByUsername(recipientUsername));

        messengerRepository.save(messengerForRecipient);
        return messengerRepository.save(messengerForOwner);
    }

    public Messenger readById(long id) {
        return messengerRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Messenger with id " + id + " not found"));
    }

    public Messenger readByOwnerAndRecipient(long ownerId, long recipientId) {
        var owner = userService.readById(ownerId);
        var recipient = userService.readById(recipientId);

        return messengerRepository.findByOwnerAndRecipient(owner, recipient).orElseThrow(() ->
                new EntityNotFoundException("Messenger for '" + owner.getName() + "' not found"));
    }

    public void delete(long id) {
        messengerRepository.delete(readById(id));
    }
}
