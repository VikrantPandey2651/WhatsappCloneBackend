package com.vikrant.whatsappclone.message;


import com.vikrant.whatsappclone.file.FileUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {


    public MessageResponse toMessageResponse(Message message) {

        return MessageResponse.builder()
                .id(message.getId())
                .state(message.getState())
                .type(message.getType())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverID())
                .createdAt(message.getCreatedDate())
                .media(FileUtils.readFileFromLocation(message.getMediaFilePath()))
                .build();
    }


}
