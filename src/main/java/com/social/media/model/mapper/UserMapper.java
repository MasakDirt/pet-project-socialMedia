package com.social.media.model.mapper;

import com.social.media.model.dto.user.UserCreateRequest;
import com.social.media.model.dto.user.UserResponse;
import com.social.media.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User createUserFromUserCreateRequest(UserCreateRequest createRequest);

    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    UserResponse createUserResponseFromUser(User user);
}
