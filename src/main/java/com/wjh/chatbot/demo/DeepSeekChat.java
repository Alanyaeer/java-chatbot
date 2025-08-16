package com.wjh.chatbot.demo;

import com.google.gson.Gson;
import com.wjh.chatbot.entity.ChatRequest;
import com.wjh.chatbot.entity.Message;
import com.wjh.chatbot.entity.enums.Role;
import com.wjh.chatbot.properties.DeepSeekProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DeepSeekChat implements CommandLineRunner {
    private final Gson gson = new Gson();
    @Resource
    private DeepSeekProperties deepSeekProperties;
    public void doChat() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Message messageSystem = Message.builder().role(Role.SYSTEM.getValue()).content("你是一个香香软软的小蛋糕").build();
        Message messageUser = Message.builder().role(Role.USER.getValue()).content("你是谁呀").build();
        List<Message> messageList = Arrays.asList(messageSystem, messageUser);
        String apiKey = deepSeekProperties.getApiKey();
        log.info("apiKey: {}", apiKey);

        ChatRequest chatRequest = ChatRequest.builder().messages(messageList).model("deepseek-chat").stream(true).build();
        String json = gson.toJson(chatRequest);
        log.info(json);
        RequestBody body = RequestBody.create(mediaType, json);
        String baseUrl = deepSeekProperties.getHost();
        DefaultUriBuilderFactory dsUri = new DefaultUriBuilderFactory(baseUrl);
        URI uri = dsUri.uriString("/chat/{type}")
                .build("completions");
        try {
            Request request = new Request.Builder()
                    .url(uri.toURL())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();
            try(Response response = client.newCall(request).execute()){
                if (response.body() != null) {
                    log.info(response.body().string());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void doFimChat(){
        String apiKey = deepSeekProperties.getApiKey();
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(5, TimeUnit.MINUTES)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n  \"model\": \"deepseek-chat\",\n  \"prompt\": \"Once upon a time, there has a huge Titan which ate human for live \",\n  \"echo\": false,\n  \"frequency_penalty\": 0,\n  \"logprobs\": 0,\n  \"max_tokens\": 128,\n  \"presence_penalty\": 0,\n  \"stop\": null,\n  \"stream\": false,\n  \"stream_options\": null,\n  \"suffix\": null,\n  \"temperature\": 1,\n  \"top_p\": 1\n}");
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/beta/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        log.info("apiKey: {}", apiKey);
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    log.info(response.body().string());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run(String... args) throws Exception {
        doFimChat();
    }
}
