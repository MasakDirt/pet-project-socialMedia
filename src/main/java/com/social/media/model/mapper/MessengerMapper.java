package com.social.media.model.mapper;

import com.social.media.model.dto.message.MessageResponse;
import com.social.media.model.dto.messenger.AllMessengersResponse;
import com.social.media.model.dto.messenger.MessengerResponse;
import com.social.media.model.entity.Message;
import com.social.media.model.entity.Messenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessengerMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(target = "lastMessage", source = "lastMessage")
    @Mapping(target = "recipientId", expression = "java(messenger.getRecipient().getId())")
    @Mapping(target = "recipientUsername", expression = "java(messenger.getRecipient().getUsername())")
    AllMessengersResponse createAllMessengersResponseFromMessenger(Messenger messenger, String lastMessage);

    @Mapping(target = "recipientId", expression = "java(messenger.getRecipient().getId())")
    @Mapping(target = "recipientUsername", expression = "java(messenger.getRecipient().getUsername())")
    @Mapping(target = "allMessages", expression = "java(refactorMessages(allMessages))")
    MessengerResponse createMessengerResponseFromMessenger(Messenger messenger, List<Message> allMessages);

    default List<MessageResponse> refactorMessages(List<Message> messages) {
        return messages
                .stream()
                .map(INSTANCE::createMessageResponseFromMessage)
                .toList();
    }
}
