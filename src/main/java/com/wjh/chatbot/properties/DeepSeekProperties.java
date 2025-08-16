package com.wjh.chatbot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@ConfigurationProperties("deepseek")
@Component
@Data
public class DeepSeekProperties {
    private String apiKey;
    private String host;
}
