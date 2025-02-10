package com.vikrant.whatsappclone.message;


import com.vikrant.whatsappclone.chat.Chat;
import com.vikrant.whatsappclone.chat.ChatRepository;
import com.vikrant.whatsappclone.file.FileService;
import com.vikrant.whatsappclone.file.FileUtils;
import com.vikrant.whatsappclone.notification.Notification;
import com.vikrant.whatsappclone.notification.NotificationService;
import com.vikrant.whatsappclone.notification.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;
    private final FileService fileService;
    private final NotificationService notificationService;

    public void saveMessage(MessageRequest messageRequest){
        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(()-> new EntityNotFoundException("No chat available with given id "+ messageRequest.getChatId()));

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setSenderId(messageRequest.getSenderId());
        message.setReceiverID(messageRequest.getReceiverId());
        message.setType(messageRequest.getType());
        message.setState(MessageState.SENT);

        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(messageRequest.getChatId())
                .messageType(messageRequest.getType())
                .content(messageRequest.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverID())
                .notificationType(NotificationType.MESSAGE)
                .messageType(messageRequest.getType())
                .build();

        notificationService.sendNotification(message.getReceiverID(),notification);


    }

    public List<MessageResponse> findChatMessage(String chatId){

        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(mapper::toMessageResponse)
                .toList();

    }

    @Transactional
    public void setMessagesToSeen(String chatId, Authentication authentication){
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(()-> new EntityNotFoundException("No Chat found with chat id "+ chatId));

        final String recipientId = getRecipientId(chat, authentication);
        messageRepository.setMessagesToSeenByChatId(chat.getId(), MessageState.SEEN);

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .senderId(getSenderId(chat,authentication))
                .receiverId(recipientId)
                .notificationType(NotificationType.SEEN)
                .build();
        notificationService.sendNotification(recipientId,notification);
    }


    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication){
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(()-> new EntityNotFoundException("No chat available with given id "+ chatId));

        final String senderId = getSenderId(chat, authentication);
        final String recipientId = getRecipientId(chat, authentication);

        final String filePath = fileService.saveFile(senderId,file);
        Message message = new Message();

        message.setChat(chat);
        message.setSenderId(senderId);
        message.setReceiverID(recipientId);
        message.setType(MessageType.IMAGE);
        message.setState(MessageState.SENT);
        message.setMediaFilePath(filePath);
        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .messageType(MessageType.IMAGE)
                .notificationType(NotificationType.IMAGE)
                .senderId(senderId)
                .receiverId(recipientId)
                .media(FileUtils.readFileFromLocation(message.getMediaFilePath()))
                .build();

        notificationService.sendNotification(recipientId,notification);



    }

    private String getRecipientId(Chat chat, Authentication authentication) {
        if(chat.getSender().getId().equals(authentication.getName())){
            return chat.getRecipient().getId();
        }
        return chat.getSender().getId();
    }

    private String getSenderId(Chat chat, Authentication authentication) {
        if(chat.getSender().getId().equals(authentication.getName())){
            return chat.getSender().getId();
        }
        return chat.getRecipient().getId();
    }


}
