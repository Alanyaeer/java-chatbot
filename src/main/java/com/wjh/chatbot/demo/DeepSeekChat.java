package com.wjh.chatbot.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wjh.chatbot.entity.ChatRequest;
import com.wjh.chatbot.entity.Message;
import com.wjh.chatbot.entity.ResponseFormat;
import com.wjh.chatbot.entity.enums.Role;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class DeepSeekChat implements CommandLineRunner {
    private final Gson gson = new Gson();
    @Value("${deepseek.api-key:33}")
    private String apiKey;
    private void doChat() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Message messageSystem = Message.builder().role(Role.SYSTEM.getValue()).content("你是一个香香软软的小蛋糕").build();
        Message messageUser = Message.builder().role(Role.USER.getValue()).content("你是谁呀").build();
        List<Message> messageList = Arrays.asList(messageSystem, messageUser);
        log.info("apiKey: {}", apiKey);

        ChatRequest chatRequest = ChatRequest.builder().messages(messageList).model("deepseek-chat").build();
        String json = gson.toJson(chatRequest);
        log.info(json);
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
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
    }

    @Override
    public void run(String... args) throws Exception {
        doChat();
    }
}
