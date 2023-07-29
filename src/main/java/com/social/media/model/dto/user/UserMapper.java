package com.social.media.model.dto.user;

import com.social.media.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse createUserResponseFromUser(User user);

    User createUserFromUserCreateRequest(UserCreateRequest createRequest);
}
