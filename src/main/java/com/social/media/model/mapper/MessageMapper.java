package com.social.media.model.mapper;

import com.social.media.model.dto.MessageResponse;
import com.social.media.model.entity.Message;
import com.social.media.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "ownerUsername", expression = "java(userService.readById(message.getOwnerId()).getUsername())")
    MessageResponse createMessageResponseFromMessage(Message message, UserService userService);
}
