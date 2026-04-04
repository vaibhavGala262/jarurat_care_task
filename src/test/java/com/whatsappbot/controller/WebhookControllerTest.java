package com.whatsappbot.controller;

import com.whatsappbot.model.OutgoingMessage;
import com.whatsappbot.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebhookControllerTest {

    private static final String VALID_JSON_PAYLOAD = """
            {
              "from": "919999999999",
              "message": "Hi",
              "messageId": "abc123"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Test
    void testValidRequestReturns200() throws Exception {
        when(messageService.processIncomingMessage(any()))
                .thenReturn(new OutgoingMessage("919999999999", "Hello!", "sent"));

        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_JSON_PAYLOAD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.to").value("919999999999"))
                .andExpect(jsonPath("$.reply").value("Hello!"))
                .andExpect(jsonPath("$.status").value("sent"));
    }

    @Test
    void testMissingFromFieldReturns400() throws Exception {
        String invalidJson = """
                {
                  "message": "Hi",
                  "messageId": "abc123"
                }
                """;

        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmptyBodyReturns400() throws Exception {
        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }
}
