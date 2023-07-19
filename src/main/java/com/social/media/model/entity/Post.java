package com.social.media.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @CreationTimestamp
    private LocalDateTime timestamp;

    @NotNull
    private File photo;

    @JsonBackReference
    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Like> likes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id && timestamp.toLocalTime().equals(post.timestamp.toLocalTime()) && Objects.equals(owner, post.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", owner=" + owner +
                '}';
    }
}
