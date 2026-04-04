package com.whatsappbot.model;

import java.util.ArrayList;
import java.util.List;

public class ConversationState {

    private final List<String> messageHistory = new ArrayList<>();
    private String userName;
    private int messageCount;

    public List<String> getMessageHistory() {
        return messageHistory;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void incrementMessageCount() {
        this.messageCount++;
    }
}
