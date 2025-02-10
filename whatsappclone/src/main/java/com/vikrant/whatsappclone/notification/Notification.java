package com.vikrant.whatsappclone.notification;


import com.vikrant.whatsappclone.message.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private String chatId;
    private String content;
    private String senderId;
    private String receiverId;
    private MessageType messageType;
    private NotificationType notificationType;
    private byte[] media;
}
