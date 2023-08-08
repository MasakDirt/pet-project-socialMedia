package com.social.media.model.dto.messenger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.media.model.dto.message.MessageResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessengerResponse {
    private long id;

    @JsonProperty("recipient_id")
    private long recipientId;

    @NotNull
    @JsonProperty("recipient_username")
    @Pattern(regexp = "^[a-z0-9-.]+$", message = "The 'username' must contains letters in lower case and can contain '-' or '.'")
    private String recipientUsername;

    @NotNull
    @JsonProperty("messages")
    private List<MessageResponse> allMessages;
}
