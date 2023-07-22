package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.Message;
import com.social.media.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessengerService messengerService;

    public Message create(long messengerId, String message) {
        checkValidMessage(message);

        var messageObj = new Message();
        messageObj.setMessage(message);
        messageObj.setMessenger(messengerService.readById(messengerId));

        return messageRepository.save(messageObj);
    }

    public Message readById(long id) {
        return messageRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Message with id " + id + " not found!"));
    }

    public Message update(long messageId, String updatedMessage) {
        checkValidMessage(updatedMessage);

        var oldMessage = readById(messageId);
        oldMessage.setMessage(updatedMessage);

        return messageRepository.save(oldMessage);
    }

    public void delete(long id) {
        messageRepository.delete(readById(id));
    }

    public Set<Message> getAll() {
        return new HashSet<>(messageRepository.findAll());
    }

    public List<Message> readAllByMessenger(long messengerId) {
        var messenger = messengerService.readById(messengerId);
        return messageRepository.findAllByMessenger(messenger);
    }

    private void checkValidMessage(String message) throws InvalidTextException {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidTextException("The message must contain the text!");
        }
    }
}
