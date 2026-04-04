package com.whatsappbot.service;

import com.whatsappbot.model.ConversationState;
import com.whatsappbot.model.IncomingMessage;
import com.whatsappbot.model.OutgoingMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private ConversationStateService conversationStateService;

    @InjectMocks
    private MessageService messageService;

    @Test
    void testHiReturnsHello() {
        IncomingMessage request = buildIncoming("919999999999", "hi", "abc123");
        when(conversationStateService.recordIncomingMessage("919999999999", "hi"))
                .thenReturn(new ConversationState());

        OutgoingMessage response = messageService.processIncomingMessage(request);

        assertTrue(response.getReply().contains("Hello"));
    }

    @Test
    void testByeReturnsGoodbye() {
        IncomingMessage request = buildIncoming("919999999999", "bye", "abc123");
        when(conversationStateService.recordIncomingMessage("919999999999", "bye"))
                .thenReturn(new ConversationState());

        OutgoingMessage response = messageService.processIncomingMessage(request);

        assertEquals("Goodbye!", response.getReply());
    }

    @Test
    void testUnknownReturnsDefault() {
        IncomingMessage request = buildIncoming("919999999999", "xyz", "abc123");
        when(conversationStateService.recordIncomingMessage("919999999999", "xyz"))
                .thenReturn(new ConversationState());

        OutgoingMessage response = messageService.processIncomingMessage(request);

        assertEquals("I didn't understand that", response.getReply());
    }

    @Test
    void testCaseInsensitive() {
        IncomingMessage upper = buildIncoming("919999999999", "HI", "abc123");
        IncomingMessage lower = buildIncoming("919999999999", "hi", "xyz789");
        when(conversationStateService.recordIncomingMessage("919999999999", "HI"))
                .thenReturn(new ConversationState());
        when(conversationStateService.recordIncomingMessage("919999999999", "hi"))
                .thenReturn(new ConversationState());

        OutgoingMessage responseUpper = messageService.processIncomingMessage(upper);
        OutgoingMessage responseLower = messageService.processIncomingMessage(lower);

        assertEquals(responseLower.getReply(), responseUpper.getReply());
    }

    @Test
    void testNullMessageHandled() {
        IncomingMessage request = buildIncoming("919999999999", null, "abc123");
        when(conversationStateService.recordIncomingMessage("919999999999", null))
                .thenReturn(new ConversationState());

        assertDoesNotThrow(() -> messageService.processIncomingMessage(request));
    }

    private IncomingMessage buildIncoming(String from, String message, String messageId) {
        IncomingMessage incomingMessage = new IncomingMessage();
        incomingMessage.setFrom(from);
        incomingMessage.setMessage(message);
        incomingMessage.setMessageId(messageId);
        return incomingMessage;
    }
}
