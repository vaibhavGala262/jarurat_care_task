package com.whatsappbot.model;

import java.time.Instant;

public class MessageLog {

    private final Instant timestamp;
    private final String from;
    private final String message;
    private final String reply;

    public MessageLog(Instant timestamp, String from, String message, String reply) {
        this.timestamp = timestamp;
        this.from = from;
        this.message = message;
        this.reply = reply;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getReply() {
        return reply;
    }
}
