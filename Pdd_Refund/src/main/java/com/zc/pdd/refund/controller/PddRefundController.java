package com.zc.pdd.refund.controller;

import com.zc.pdd.refund.enums.ChatStatus;
import com.zc.pdd.refund.model.OrderChat;
import com.zc.pdd.refund.tools.OrderTools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/pdd/refund")
public class PddRefundController {

    @Resource
    private ChatClient chatClient;

    @Resource
    private OrderTools orderTools;

    @GetMapping("/newChat")
    public OrderChat newChat(@RequestParam String userId,
                             @RequestParam String orderId,
                             HttpServletResponse httpServletResponse) {
        httpServletResponse.setCharacterEncoding("UTF-8");

        String chatId = UUID.randomUUID().toString();

        return chatClient
                .prompt()
                .user(String.format(
                        "我要咨询订单相关的售后问题，我的用户id是%s,我的订单号是: %s ,本地的对话Id是 %s，当前状态是 %s",
                        userId, orderId, chatId, ChatStatus.CHAT_START.name()))
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param("chat_memory_retrieve_size", 100))
                .call()
                .entity(OrderChat.class);
    }

    @GetMapping("/ask")
    public Flux<String> ask(String question, String chatId, HttpServletResponse httpServletResponse) {
        httpServletResponse.setCharacterEncoding("UTF-8");

        return chatClient
                .prompt()
                .user(question).tools(orderTools)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param("chat_memory_retrieve_size", 100))
                .stream().content();
    }
}
