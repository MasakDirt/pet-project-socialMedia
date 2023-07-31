package com.social.media.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordRequest {
    @JsonProperty("old_password")
    @NotBlank(message = "The 'password' cannot be blank!")
    private String oldPassword;

    @JsonProperty("new_password")
    @NotBlank(message = "The 'password' cannot be blank!")
    private String newPassword;
}
