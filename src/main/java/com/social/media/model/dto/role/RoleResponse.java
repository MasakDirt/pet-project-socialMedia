package com.social.media.model.dto.role;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
@AllArgsConstructor
public class RoleResponse {
    private long id;

    @NotNull
    @Pattern(regexp = "^[A-Z._-]+$")
    private String name;
}
