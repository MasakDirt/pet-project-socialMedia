package com.social.media.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import static com.social.media.model.entity.User.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private long id;

    @NotNull
    @Pattern(regexp = "^[a-z0-9-.]+$", message = "The 'username' must contains letters in lower case and can contain '-' or '.'")
    private String username;

    @NotNull
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    private String email;

    @NotNull
    @JsonProperty("first_name")
    @Pattern(regexp = NAME_REGEXP,
            message = "First name must start with a capital letter and followed by one or more lowercase")
    private String firstName;

    @NotNull
    @JsonProperty("last_name")
    @Pattern(regexp = NAME_REGEXP,
            message = "Last name must start with a capital letter and followed by one or more lowercase")
    private String lastName;

    @JsonProperty("old_password")
    @NotBlank(message = "The 'password' cannot be blank!")
    private String oldPassword;

    @JsonProperty("new_password")
    @NotBlank(message = "The 'password' cannot be blank!")
    private String newPassword;
}
