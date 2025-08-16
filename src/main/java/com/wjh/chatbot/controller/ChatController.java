package com.wjh.chatbot.controller;

import com.wjh.chatbot.demo.DeepSeekChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private DeepSeekChat deepSeekChat;
    @PostMapping("/custom")
    public String completionChat(){
        deepSeekChat.doChat();
        return "ok";
    }

    @PostMapping("/fim")
    public String fimChat(){
        deepSeekChat.doFimChat();
        return "ok";
    }
}
