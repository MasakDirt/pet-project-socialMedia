package com.social.media.model.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String id;

    @JsonProperty("owner_id")
    private long ownerId;

    @NotBlank
    private String message;

    @NotNull
    private LocalDateTime timestamp;
}