package com.snowyowl.lechat.websocket.chat;

import com.snowyowl.lechat.websocket.room.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage message) {
        String chatId = chatRoomService.getChatRoomId(
                message.getSenderId(),
                message.getRecipientId(),
                true
        ).orElseThrow(); // TODO: create a dedicated Exception

        message.setChatId(chatId);
        repository.save(message);

        return message;
    }

    public List<ChatMessage> findChatMessages(
            String senderId,
            String recipientId
    ) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);

        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }
}
