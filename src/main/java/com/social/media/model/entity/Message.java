package com.social.media.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false)
    private String message;

    @NotNull
    @CreationTimestamp
    private LocalDateTime timestamp;

    @JsonBackReference
    @JoinColumn(name = "messenger_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Messenger messenger;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message messageObj = (Message) o;
        return id == messageObj.id && Objects.equals(message, messageObj.message) &&
                Objects.equals(timestamp.toLocalTime(), messageObj.timestamp.toLocalTime()) && Objects.equals(messenger, messageObj.messenger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, messenger);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", messenger=" + messenger +
                '}';
    }
}
