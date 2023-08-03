package com.social.media.service;

import com.google.common.collect.Iterables;
import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.Message;
import com.social.media.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessengerService messengerService;

    public Message create(long messengerId, long ownerId, String message) {
        checkValidMessage(message);
        checkValidMessengerId(messengerId);

        var messageObj = new Message();
        messageObj.setMessage(message);
        messageObj.setMessengerId(messengerId);
        messageObj.setOwnerId(ownerId);

        return messageRepository.save(messageObj);
    }

    public Message readById(String id) {
        return messageRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Message with id " + id + " not found!"));
    }

    public Message update(String messageId, String updatedMessage) {
        checkValidMessage(updatedMessage);

        var oldMessage = readById(messageId);
        oldMessage.setMessage(updatedMessage);

        return messageRepository.save(oldMessage);
    }

    public void delete(String id) {
        messageRepository.delete(readById(id));
    }

    public Set<Message> getAll() {
        return new HashSet<>(messageRepository.findAll());
    }

    public List<Message> readAllByMessenger(long messengerId) {
        checkValidMessengerId(messengerId);

        return messageRepository.findAllByMessengerId(messengerId);
    }

    public List<Message> getAllByMessenger(long ownerMessengerId) {
        var ownerMessenger = messengerService.readById(ownerMessengerId);
        var recipientMessenger = messengerService.readByOwnerAndRecipient(ownerMessenger.getRecipient().getId(), ownerMessenger.getOwner().getId());

        var ownersMessages = readAllByMessenger(ownerMessengerId).stream();
        var recipientMessages = readAllByMessenger(recipientMessenger.getId()).stream();

        var messages = Stream.concat(ownersMessages, recipientMessages).toList();

        return messages.isEmpty() ? new ArrayList<>() : messages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .toList();
    }

    public String getLastMessage(long ownerMessengerId) {
        var messages = getAllByMessenger(ownerMessengerId);

        return messages.isEmpty() ? "" : Iterables.getLast(messages).getMessage();
    }

    private void checkValidMessage(String message) throws InvalidTextException {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidTextException("The message must contain the text!");
        }
    }

    private void checkValidMessengerId(long messengerId) throws EntityNotFoundException {
        if (messengerId < 1) {
            throw new EntityNotFoundException("Messenger with id " + messengerId + " not found!");
        }
    }
}
