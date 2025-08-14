package com.wjh.chatbot.entity;

import com.wjh.chatbot.entity.enums.Role;
import lombok.Builder;

@Builder
public class Message {
    private String content;
    private String role;

    // Constructors
    public Message() {}

    public Message(String content, String role) {
        this.content = content;
        this.role = role;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = String.valueOf(role);
    }
}