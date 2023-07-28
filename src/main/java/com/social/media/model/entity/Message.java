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
    @Id
    private String id;

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
        Message message1 = (Message) o;
        return messengerId == message1.messengerId && Objects.equals(id, message1.id) &&
                Objects.equals(message, message1.message) && Objects.equals(timestamp.toLocalDate(), message1.timestamp.toLocalDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, timestamp, messengerId);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", messengerId=" + messengerId +
                '}';
    }
}
