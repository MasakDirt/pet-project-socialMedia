package com.social.media.model.mapper;

import com.social.media.model.dto.message.MessageResponse;
import com.social.media.model.entity.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse createMessageResponseFromMessage(Message message);
}
