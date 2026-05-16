package com.zc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(12);

    private final ObjectMapper objectMapper;

    public WeatherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Tool(description = "根据城市名称获取当前天气概况（温度与天气描述）；网络不可用时返回示例数据。")
    public String getWeatherByCity(
            @ToolParam(description = "城市中文或英文名称，例如：上海、Beijing") String city) {
        if (!StringUtils.hasText(city)) {
            return "请提供有效的城市名称。";
        }
        String trimmed = city.trim();
        try {
            String encoded = UriUtils.encodePathSegment(trimmed, StandardCharsets.UTF_8);
            URI uri = URI.create("https://wttr.in/" + encoded + "?format=j1");
            HttpClient client = HttpClient.newBuilder().connectTimeout(HTTP_TIMEOUT).build();
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(HTTP_TIMEOUT)
                    .header("User-Agent", "zc-mcp-server/1.0 (Spring AI MCP)")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 || !StringUtils.hasText(response.body())) {
                return fallback(trimmed, "上游返回 HTTP " + response.statusCode());
            }
            return parseWttrJson(trimmed, response.body());
        }
        catch (Exception ex) {
            log.warn("Weather lookup failed for city='{}': {}", trimmed, ex.toString());
            return fallback(trimmed, ex.getMessage());
        }
    }

    private String parseWttrJson(String city, String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        JsonNode currentList = root.path("current_condition");
        if (!currentList.isArray() || currentList.isEmpty()) {
            return fallback(city, "响应中缺少 current_condition");
        }
        JsonNode current = currentList.get(0);
        String tempC = current.path("temp_C").asText("?");
        JsonNode descArr = current.path("weatherDesc");
        String desc = descArr.isArray() && !descArr.isEmpty()
                ? descArr.get(0).path("value").asText("未知")
                : "未知";
        return String.format("%s：当前约 %s°C，%s（数据来源 wttr.in）", city, tempC, desc);
    }

    private String fallback(String city, String reason) {
        return city + " 天气暂不可用（" + reason + "）。示例：晴朗，气温约 25℃，适合出行。";
    }
}
