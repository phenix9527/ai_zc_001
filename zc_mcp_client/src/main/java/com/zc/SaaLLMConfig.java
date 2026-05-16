package com.zc;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaaLLMConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ObjectProvider<ToolCallbackProvider> toolCallbacksProvider) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        toolCallbacksProvider.ifAvailable(tools -> builder.defaultToolCallbacks(tools.getToolCallbacks()));
        return builder.build();
    }
}