package com.social.media.model.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestWithUsername {
    @NotNull(message = "The 'username' cannot be null!")
    @Pattern(regexp = "^[a-z0-9-.]+$", message = "The 'username' must contains letters in lower case and can contain '-' or '.'")
    private String username;

    @NotNull(message = "The 'password' cannot be blank!")
    private String password;
}
