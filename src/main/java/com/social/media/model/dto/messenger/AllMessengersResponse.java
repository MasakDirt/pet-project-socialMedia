package com.social.media.model.dto.messenger;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllMessengersResponse {
    private long id;

    @JsonProperty("recipient_id")
    private long recipientId;

    @JsonProperty("recipient_username")
    @NotNull
    @Pattern(regexp = "^[a-z0-9-.]+$", message = "The 'username' must contains letters in lower case and can contain '-' or '.'")
    private String recipientUsername;

    @NotBlank
    @JsonProperty("last_message")
    private String lastMessage;
}
