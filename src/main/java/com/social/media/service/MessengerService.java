package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.exception.SameUsersException;
import com.social.media.model.entity.Messenger;
import com.social.media.repository.MessengerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class MessengerService {
    private final MessengerRepository messengerRepository;
    private final UserService userService;

    private static final SameUsersException SAME_USERS_EXCEPTION = new SameUsersException("You cannot create messenger for you, so write please, another user!");

    public Messenger create(long ownerId, long recipientId) {
        if (ownerId == recipientId){
            throw SAME_USERS_EXCEPTION;
        }

        var messengerForOwner = createNewMessengerId(ownerId, recipientId);

        var messengerForRecipient = createNewMessengerId(recipientId, ownerId);

        messengerRepository.save(messengerForRecipient);
        return messengerRepository.save(messengerForOwner);
    }

    public Messenger create(long ownerId, String recipientUsername) {
        if (recipientUsername == null || recipientUsername.trim().isEmpty()) {
            throw new InvalidTextException("Username must contains at least one letter!");
        }

        var owner = userService.readById(ownerId);
        var recipient = userService.readByUsername(recipientUsername);

        if (owner.equals(recipient)){
            throw SAME_USERS_EXCEPTION;
        }

        var messengerForOwner = new Messenger();
        messengerForOwner.setOwner(owner);
        messengerForOwner.setRecipient(recipient);

        var messengerForRecipient = new Messenger();
        messengerForRecipient.setOwner(recipient);
        messengerForRecipient.setRecipient(owner);

        messengerRepository.save(messengerForRecipient);
        return messengerRepository.save(messengerForOwner);
    }

    public Messenger readById(long id) {
        return messengerRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Messenger with id " + id + " not found"));
    }

    public Messenger readByOwnerAndRecipient(long ownerId, long recipientId) {
        return messengerRepository.findByOwnerIdAndRecipientId(ownerId, recipientId);
    }

    public Set<Messenger> getAll() {
        return new HashSet<>(messengerRepository.findAll());
    }

    public void delete(long id) {
        messengerRepository.delete(readById(id));
    }

    public List<Messenger> getAllByOwnerId(long ownerId) {
        return messengerRepository.findAllByOwnerId(ownerId);
    }

    private Messenger createNewMessengerId(long ownerId, long recipientId) {
        var messenger = new Messenger();
        messenger.setOwner(userService.readById(ownerId));
        messenger.setRecipient(userService.readById(recipientId));

        return messenger;
    }
}
