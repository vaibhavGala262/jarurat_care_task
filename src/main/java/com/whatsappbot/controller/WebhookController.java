package com.whatsappbot.controller;

import com.whatsappbot.model.IncomingMessage;
import com.whatsappbot.model.OutgoingMessage;
import com.whatsappbot.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebhookController {

    private final MessageService messageService;

    public WebhookController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<OutgoingMessage> handleIncomingMessage(@Valid @RequestBody IncomingMessage incomingMessage) {
        return ResponseEntity.ok(messageService.processIncomingMessage(incomingMessage));
    }
}
