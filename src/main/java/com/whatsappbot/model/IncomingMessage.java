package com.whatsappbot.model;

import jakarta.validation.constraints.NotBlank;

public class IncomingMessage {

    @NotBlank(message = "from is required")
    private String from;

    @NotBlank(message = "message is required")
    private String message;

    @NotBlank(message = "messageId is required")
    private String messageId;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
