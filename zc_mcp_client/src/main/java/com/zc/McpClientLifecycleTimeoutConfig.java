package com.zc;

import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.ai.mcp.customizer.McpAsyncClientCustomizer;
import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mcp-core 将「初始化握手」与「普通请求」超时分开；Spring AI 自动配置只设置了 requestTimeout，
 * initialization 仍默认 20s，stdio/npx 首次拉包易触发 TimeoutException。
 */
@Configuration
public class McpClientLifecycleTimeoutConfig {

    @Bean
    public McpSyncClientCustomizer mcpSyncInitializationTimeoutCustomizer(McpClientCommonProperties properties) {
        return (name, spec) -> spec.initializationTimeout(properties.getRequestTimeout());
    }

    @Bean
    public McpAsyncClientCustomizer mcpAsyncInitializationTimeoutCustomizer(McpClientCommonProperties properties) {
        return (name, spec) -> spec.initializationTimeout(properties.getRequestTimeout());
    }
}
