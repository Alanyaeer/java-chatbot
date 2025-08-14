package com.wjh.chatbot.entity.enums;

import lombok.Getter;

@Getter
public enum ToolChoice {
    NONE("none"),
    AUTO("auto");

    private final String value;

    ToolChoice(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ToolChoice fromValue(String value) {
        for (ToolChoice choice : ToolChoice.values()) {
            if (choice.value.equals(value)) {
                return choice;
            }
        }
        throw new IllegalArgumentException("Unknown tool choice: " + value);
    }
}