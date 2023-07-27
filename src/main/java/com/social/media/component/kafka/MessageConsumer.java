package com.social.media.component.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    @KafkaListener(topics = "user-messages", groupId = "message-consumer-group")
    public void receiveMessage(@Header(KafkaHeaders.RECEIVED_KEY) String senderUsername, String message) {
        String recipientUsername = message.substring(0, message.indexOf(':'));
    }
}
