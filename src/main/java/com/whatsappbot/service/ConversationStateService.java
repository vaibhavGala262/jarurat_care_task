package com.whatsappbot.service;

import com.whatsappbot.model.ConversationState;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConversationStateService {

    private static final Pattern NAME_PATTERN = Pattern
            .compile("(?i)^my\\s+name\\s+is\\s+([a-zA-Z][a-zA-Z\\s'-]{0,48})$");

    private final Map<String, ConversationState> userStateMap = new ConcurrentHashMap<>();
    private final Map<String, LongAdder> keywordFrequency = new ConcurrentHashMap<>();
    private final AtomicLong totalMessages = new AtomicLong();
    private volatile Instant lastMessageAt;

    public ConversationState recordIncomingMessage(String from, String message) {
        ConversationState state = getOrCreateState(from);
        String safeMessage = message == null ? "" : message.trim();

        synchronized (state) {
            if (!safeMessage.isEmpty()) {
                state.getMessageHistory().add(safeMessage);
            }
            state.incrementMessageCount();
            maybeUpdateUserName(state, safeMessage);
        }

        totalMessages.incrementAndGet();
        lastMessageAt = Instant.now();
        trackKeywords(safeMessage);
        return state;
    }

    public ConversationState getOrCreateState(String from) {
        return userStateMap.computeIfAbsent(from == null ? "unknown" : from, key -> new ConversationState());
    }

    public List<String> getLastMessages(String from, int limit) {
        ConversationState state = getOrCreateState(from);
        synchronized (state) {
            int size = state.getMessageHistory().size();
            int start = Math.max(0, size - limit);
            return new ArrayList<>(state.getMessageHistory().subList(start, size));
        }
    }

    public ConversationStats getStats() {
        return new ConversationStats(userStateMap.size(), totalMessages.get());
    }

    public List<String> getTopKeywords(int topN) {
        return keywordFrequency.entrySet().stream()
                .sorted(Comparator.comparingLong((Map.Entry<String, LongAdder> entry) -> entry.getValue().longValue())
                        .reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    private void maybeUpdateUserName(ConversationState state, String message) {
        Matcher matcher = NAME_PATTERN.matcher(message);
        if (matcher.matches()) {
            state.setUserName(matcher.group(1).trim());
        }
    }

    private void trackKeywords(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        String normalized = message.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] words = normalized.trim().split("\\s+");
        for (String word : words) {
            if (!word.isBlank()) {
                keywordFrequency.computeIfAbsent(word, key -> new LongAdder()).increment();
            }
        }
    }

    public static class ConversationStats {

        private final long totalUsers;
        private final long totalMessages;

        public ConversationStats(long totalUsers, long totalMessages) {
            this.totalUsers = totalUsers;
            this.totalMessages = totalMessages;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getTotalMessages() {
            return totalMessages;
        }
    }
}
