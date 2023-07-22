package com.social.media.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "messengers")
public class Messenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    @JsonBackReference
    @JoinColumn(name = "recipient_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User recipient;

    @JsonManagedReference
    @OneToMany(mappedBy = "messenger", cascade = CascadeType.ALL)
    private List<Message> messages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Messenger messenger = (Messenger) o;
        return id == messenger.id && Objects.equals(owner, messenger.owner) && Objects.equals(recipient, messenger.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, recipient);
    }

    @Override
    public String toString() {
        return "Messenger{" +
                "id=" + id +
                ", owner=" + owner +
                ", recipient=" + recipient +
                '}';
    }
}
