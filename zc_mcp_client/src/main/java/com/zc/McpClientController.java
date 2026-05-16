package com.zc;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpClientController {
    @Resource
    private ChatClient chatClient;

    @GetMapping(value = "/ask-weather", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String askWeather(@RequestParam String question) {
        // 比如传入 question = "北京今天天气怎么样？"
        return chatClient.prompt(question).call().content();
    }



}