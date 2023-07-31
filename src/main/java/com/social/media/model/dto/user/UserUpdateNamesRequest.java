package com.social.media.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import static com.social.media.model.entity.User.NAME_REGEXP;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateNamesRequest {
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
}
