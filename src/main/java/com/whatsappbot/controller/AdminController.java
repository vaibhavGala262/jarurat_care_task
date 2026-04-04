package com.whatsappbot.controller;

import com.whatsappbot.service.ConversationStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ConversationStateService conversationStateService;
    private Instant appStartTime;

    @Value("${bot.admin.token}")
    private String expectedAdminToken;

    public AdminController(ConversationStateService conversationStateService) {
        this.conversationStateService = conversationStateService;
    }

    @PostConstruct
    public void init() {
        this.appStartTime = Instant.now();
    }

    @GetMapping("/stats")
    @Operation(summary = "Get chatbot statistics", description = "Returns message metrics and basic runtime stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stats returned successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid admin token", content = @Content(schema = @Schema(example = "{\"message\":\"Unauthorized\"}"))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestHeader(name = "X-Admin-Token", required = false) String adminToken) {

        if (adminToken == null || !adminToken.equals(expectedAdminToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
        }

        ConversationStateService.ConversationStats stats = conversationStateService.getStats();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalMessages", stats.getTotalMessages());
        response.put("uniqueUsers", stats.getTotalUsers());
        response.put("topKeywords", conversationStateService.getTopKeywords(3));
        response.put("serverUptime", formatUptime(Duration.between(appStartTime, Instant.now())));
        response.put("lastMessageAt", conversationStateService.getLastMessageAt() == null
                ? null
                : conversationStateService.getLastMessageAt().toString());

        return ResponseEntity.ok(response);
    }

    private String formatUptime(Duration duration) {
        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return hours + "h " + minutes + "m";
    }
}
