package com.social.media.model.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseForPost {
    private long id;

    @NotBlank(message = "Comment cannot be blank!")
    private String comment;

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    @JsonProperty("username")
    @Pattern(regexp = "^[a-z0-9-.]+$", message = "The 'username' must contains letters in lower case and can contain '-' or '.'")
    private String ownerUsername;
}
