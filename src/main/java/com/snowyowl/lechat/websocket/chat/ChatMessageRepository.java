package com.snowyowl.lechat.websocket.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String chatId);
}
