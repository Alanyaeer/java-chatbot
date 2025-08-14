package com.wjh.chatbot.entity;

import com.wjh.chatbot.entity.enums.ResponseFormatType;

public class ResponseFormat {
    private ResponseFormatType type;

    // Constructors
    public ResponseFormat() {}

    public ResponseFormat(ResponseFormatType type) {
        this.type = type;
    }

    // Getters and Setters
    public ResponseFormatType getType() {
        return type;
    }

    public void setType(ResponseFormatType type) {
        this.type = type;
    }
}
