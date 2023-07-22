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
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Comment cannot be blank!")
    @Column(nullable = false)
    private String comment;

    @NotNull
    @CreationTimestamp
    private LocalDateTime timestamp;

    @JsonBackReference
    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    @JsonBackReference
    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    public Comment(){
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment commentObj = (Comment) o;
        return id == commentObj.id && Objects.equals(comment, commentObj.comment) &&
                Objects.equals(timestamp.toLocalDate(), commentObj.timestamp.toLocalDate()) && Objects.equals(owner, commentObj.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comment, timestamp, owner);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", timestamp=" + timestamp +
                ", owner=" + owner +
                '}';
    }
}
