package com.wjh.chatbot.entity;

import com.wjh.chatbot.entity.enums.ToolChoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@AllArgsConstructor
public class ChatRequest {
    private List<Message> messages;
    private String model;
    private Double frequencyPenalty;
    private Integer maxTokens;
    private Double presencePenalty;
    private ResponseFormat responseFormat;
    private List<String> stop;
    private Boolean stream;
    private Object streamOptions; // Can be more specific if needed
    private Double temperature;
    private Double topP;
    private List<Object> tools; // Can be more specific if needed
    private ToolChoice toolChoice;
    private Boolean logprobs;
    private Integer topLogprobs;

    // Constructors
    public ChatRequest() {}

    // Getters and Setters
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public ResponseFormat getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(ResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
    }

    public List<String> getStop() {
        return stop;
    }

    public void setStop(List<String> stop) {
        this.stop = stop;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public Object getStreamOptions() {
        return streamOptions;
    }

    public void setStreamOptions(Object streamOptions) {
        this.streamOptions = streamOptions;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public List<Object> getTools() {
        return tools;
    }

    public void setTools(List<Object> tools) {
        this.tools = tools;
    }

    public ToolChoice getToolChoice() {
        return toolChoice;
    }

    public void setToolChoice(ToolChoice toolChoice) {
        this.toolChoice = toolChoice;
    }

    public Boolean getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Boolean logprobs) {
        this.logprobs = logprobs;
    }

    public Integer getTopLogprobs() {
        return topLogprobs;
    }

    public void setTopLogprobs(Integer topLogprobs) {
        this.topLogprobs = topLogprobs;
    }
}