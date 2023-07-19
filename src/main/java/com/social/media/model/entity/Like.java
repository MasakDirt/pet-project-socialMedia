package com.social.media.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    @JsonBackReference
    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return id == like.id && Objects.equals(owner, like.owner) && Objects.equals(post, like.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, post);
    }

    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", post=" + post +
                '}';
    }
}
