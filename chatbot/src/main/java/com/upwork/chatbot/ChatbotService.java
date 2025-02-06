package com.upwork.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
class ChatbotService {

    private final ChatClient chatClient;

    public ChatbotService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ChatResponse chat(ChatRequest chatRequest) {
        UUID chatId = Optional.ofNullable(chatRequest.chatId())
            .orElse(UUID.randomUUID());
        String answer = chatClient.prompt()
            .user(chatRequest.question())
            .advisors(spec ->
                spec.param("chat_memory_conversation_id", chatId))
            .call()
            .chatResponse()
            .getResult()
            .getOutput()
            .getContent();
        return new ChatResponse(chatId, answer);
    }

}