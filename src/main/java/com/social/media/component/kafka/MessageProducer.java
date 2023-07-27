package com.social.media.component.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MessageProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String senderUsername, String recipientUsername, String message) {
        String payload = recipientUsername + ": " + message;
        kafkaTemplate.send("user-messages",senderUsername, payload);
    }
}
