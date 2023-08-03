package com.social.media.model.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @NotNull
    private String description;

    @NotNull
    private List<String> photos;
}
