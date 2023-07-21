package com.social.media.service;

import com.social.media.model.entity.Message;
import com.social.media.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessengerService messengerService;

    public Message create(long messengerId, String message) {
        var messageObj = new Message();
        messageObj.setMessage(message);
        messageObj.setMessenger(messengerService.readById(messengerId));

        return messageRepository.save(messageObj);
    }

    public Message readById(long id) {
        return messageRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Message with id " + id + " not found!"));
    }

    public Message update(Message updatedMessage){
        readById(updatedMessage.getId());
        return messageRepository.save(updatedMessage);
    }

    public void delete(long id) {
        messageRepository.delete(readById(id));
    }
}
