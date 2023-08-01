package com.social.media.model.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.media.model.entity.Photo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseForOwner {
    private long id;

    @JsonProperty("post_id")
    private long postId;

    @NotBlank(message = "Comment cannot be blank!")
    private String comment;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    private List<Photo> photos;
}
