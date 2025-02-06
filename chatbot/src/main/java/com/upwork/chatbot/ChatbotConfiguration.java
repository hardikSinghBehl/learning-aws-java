package com.upwork.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
class ChatbotConfiguration {

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public ChatClient chatClient(
        ChatMemory chatMemory,
        ChatModel chatModel,
        @Value("classpath:system-prompt.st") Resource systemPrompt) {
        return ChatClient
            .builder(chatModel)
            .defaultAdvisors(
                new MessageChatMemoryAdvisor(chatMemory),
                new SimpleLoggerAdvisor()
            )
            .defaultSystem(systemPrompt)
            .build();
    }

}