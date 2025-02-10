package com.vikrant.whatsappclone.chat;


import com.vikrant.whatsappclone.user.User;
import com.vikrant.whatsappclone.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final ChatMapper chatMapper;

    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByReceiverId(Authentication currentUser){
        final String userId = currentUser.getName();
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map(chat-> chatMapper.toChatResponse(chat, userId))
                .toList();
    }

    public String createChat(String senderId, String receiverId){

        Optional<Chat> existingChat = chatRepository.findChatByReceiverAndSender(senderId, receiverId);
        if(existingChat.isPresent()){
            return existingChat.get().getId();
        }

        User sender = userRepository.findByPublicId(senderId)
                .orElseThrow(()-> new EntityNotFoundException("User with id"+ senderId + " not found"));
        User receiver = userRepository.findByPublicId(receiverId)
                .orElseThrow(()-> new EntityNotFoundException("User with id"+ receiverId + " not found"));

        Chat chat = new Chat();
        chat.setRecipient(receiver);
        chat.setSender(sender);

        Chat savedChat = chatRepository.save(chat);

        return savedChat.getId();
    }
}
