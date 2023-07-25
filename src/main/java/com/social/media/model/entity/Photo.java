package com.social.media.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private File file;

    @JsonBackReference
    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return id == photo.id && Objects.equals(file, photo.file) && Objects.equals(post, photo.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, file, post);
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", file=" + file +
                ", post=" + post +
                '}';
    }
}
