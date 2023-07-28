package com.social.media.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Document(collection = "messages")
public class Message {

    @NotBlank
    @Column(nullable = false)
    private String message;

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    private long messengerId;

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message messageObj = (Message) o;
        return Objects.equals(message, messageObj.message) &&
                Objects.equals(timestamp.toLocalDate(), messageObj.timestamp.toLocalDate()) && this.messengerId == messageObj.messengerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, messengerId);
    }

    @Override
    public String toString() {
        return "Message{" +
                ", text='" + message +
                '}';
    }
}
