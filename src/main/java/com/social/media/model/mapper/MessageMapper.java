package com.social.media.model.mapper;

import com.social.media.model.dto.MessageResponse;
import com.social.media.model.entity.Message;
import com.social.media.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse createMessageResponseFromMessage(Message message);
}
