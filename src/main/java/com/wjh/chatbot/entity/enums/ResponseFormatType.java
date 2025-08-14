package com.wjh.chatbot.entity.enums;

import lombok.Getter;

@Getter

public enum ResponseFormatType {
    TEXT("text"),
    JSON_OBJECT("json_object");

    private final String value;

    ResponseFormatType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ResponseFormatType fromValue(String value) {
        for (ResponseFormatType type : ResponseFormatType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown response format type: " + value);
    }
}
