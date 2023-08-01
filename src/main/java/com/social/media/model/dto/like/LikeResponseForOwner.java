package com.social.media.model.dto.like;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.media.model.entity.Photo;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseForOwner {
    private long id;

    @JsonProperty("post_id")
    private long postId;

    @NotNull
    private String description;

    @NotNull
    private List<Photo> photos;
}
