package com.social.media.model.mapper;

import com.social.media.model.dto.user.*;
import com.social.media.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User createUserFromUserCreateRequest(UserCreateRequest createRequest);

    @Mapping(target = "role", ignore = true)
    User createUserFromUserCreateRequestWithRole(UserCreateRequestWithRole requestWithRole);

    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    UserResponse createUserResponseFromUser(User user);

    @Mapping(target = "password", source = "newPassword")
    User createUserFromUserUpdateRequestById(UserUpdateRequest requestById);

    @Mapping(target = "password", source = "newPassword")
    User createUserFromUserUpdateRequestByUsername(UserUpdateRequest requestByUsername);

    @Mapping(target = "password", source = "newPassword")
    User createUserFromUserUpdateRequestByEmail(UserUpdateRequest requestByEmail);
}
