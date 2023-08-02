package com.social.media.model.dto.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.File;
import java.time.LocalDateTime;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponse {
    private long id;

    @JsonProperty("post_id")
    private long postId;

    @NotNull
    private File file;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime timestamp;
}
