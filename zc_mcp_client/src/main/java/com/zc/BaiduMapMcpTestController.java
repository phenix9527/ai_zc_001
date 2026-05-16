package com.zc;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/map-test")
public class BaiduMapMcpTestController {

    @Resource
    private ChatClient chatClient; // 注入了 MCP 工具的 ChatClient

    @Resource
    private ChatModel chatModel;   // 普通的大模型（没有工具能力）

    /**
     * 接口1：带有百度地图 MCP 能力的智能对话
     * 测试示例：/map-test/chat?msg=帮我规划从北京西站到故宫的驾车路线
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public Flux<String> chatWithMcp(@RequestParam(name = "msg") String msg) {
        // 使用流式输出，让回复像打字机一样呈现
        return chatClient.prompt(msg).stream().content();
    }

    /**
     * 接口2：普通大模型对话（用于对比测试）
     * 测试示例：/map-test/chat-normal?msg=帮我规划从北京西站到故宫的驾车路线
     */
    @GetMapping(value = "/chat-normal", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public Flux<String> chatNormal(@RequestParam(name = "msg") String msg) {
        // 普通模型没有接入百度地图，只能靠训练数据瞎编或给出笼统建议
        return chatModel.stream(msg);
    }
}