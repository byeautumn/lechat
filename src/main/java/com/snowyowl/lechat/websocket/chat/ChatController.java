package com.snowyowl.lechat.websocket.chat;

import com.snowyowl.lechat.websocket.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.snowyowl.lechat.websocket.user.User;

import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;



    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) {
        if (null == message) {
            System.out.println("The input chat message is null.");
            return;
        }
        ChatMessage savedMessage = chatMessageService.save(message);

        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/messages",
                ChatNotification.builder()
                        .id(savedMessage.getId())
                        .senderId(savedMessage.getSenderId())
                        .recipientId(savedMessage.getRecipientId())
                        .content(savedMessage.getContent())
                        .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findMessages(
            @PathVariable("senderId") String senderId,
            @PathVariable("recipientId") String recipientId
    ) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

}