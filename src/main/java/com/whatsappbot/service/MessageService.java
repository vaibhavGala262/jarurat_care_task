package com.whatsappbot.service;

import com.whatsappbot.model.ConversationState;
import com.whatsappbot.model.IncomingMessage;
import com.whatsappbot.model.MessageLog;
import com.whatsappbot.model.OutgoingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private static final String FALLBACK_REPLY = "I didn't understand that";
    private static final String HELP_REPLY = "Available commands: Hi, Bye, Help, history, My name is <name>";

    private final ConversationStateService conversationStateService;

    public MessageService(ConversationStateService conversationStateService) {
        this.conversationStateService = conversationStateService;
    }

    public OutgoingMessage processIncomingMessage(IncomingMessage incomingMessage) {
        String from = incomingMessage.getFrom();
        String rawMessage = incomingMessage.getMessage();

        ConversationState state = conversationStateService.recordIncomingMessage(from, rawMessage);
        String reply = determineReply(rawMessage, from, state);

        MessageLog messageLog = new MessageLog(Instant.now(), from, rawMessage, reply);
        LOGGER.info("incoming_message timestamp={} from={} message=\"{}\" reply=\"{}\"",
                messageLog.getTimestamp(), messageLog.getFrom(), messageLog.getMessage(), messageLog.getReply());

        return new OutgoingMessage(from, reply, "sent");
    }

    private String determineReply(String message, String from, ConversationState state) {
        String normalized = message == null ? "" : message.trim();

        if (normalized.equalsIgnoreCase("hi")) {
            if (state.getUserName() != null && !state.getUserName().isBlank()) {
                return "Hello " + state.getUserName() + "!";
            }
            return "Hello!";
        }

        if (normalized.equalsIgnoreCase("bye")) {
            return "Goodbye!";
        }

        if (normalized.equalsIgnoreCase("help")) {
            return HELP_REPLY;
        }

        if (normalized.equalsIgnoreCase("history")) {
            List<String> lastMessages = conversationStateService.getLastMessages(from, 5);
            if (lastMessages.isEmpty()) {
                return "No message history yet.";
            }
            return "Your last messages: " + String.join(" | ", lastMessages);
        }

        if (normalized.toLowerCase().startsWith("my name is")) {
            if (state.getUserName() != null && !state.getUserName().isBlank()) {
                return "Nice to meet you, " + state.getUserName() + "!";
            }
            return "Nice to meet you!";
        }

        return FALLBACK_REPLY;
    }
}
