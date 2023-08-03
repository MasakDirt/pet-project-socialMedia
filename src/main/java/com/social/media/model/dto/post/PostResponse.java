package com.social.media.model.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.media.model.entity.Photo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private long id;

    @NotNull
    @JsonProperty("username")
    @Pattern(regexp = "^[a-z0-9-.]+$", message = "The 'username' must contains letters in lower case and can contain '-' or '.'")
    private String ownerUsername;

    @NotNull
    @CreationTimestamp
    private LocalDateTime timestamp;

    @NotNull
    private String description;

    private int comments;

    private int likes;

    @NotNull
    private List<Photo> photos;
}
