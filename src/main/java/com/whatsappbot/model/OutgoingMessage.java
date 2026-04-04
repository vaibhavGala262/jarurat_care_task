package com.whatsappbot.model;

public class OutgoingMessage {

    private String to;
    private String reply;
    private String status;

    public OutgoingMessage() {
    }

    public OutgoingMessage(String to, String reply, String status) {
        this.to = to;
        this.reply = reply;
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
