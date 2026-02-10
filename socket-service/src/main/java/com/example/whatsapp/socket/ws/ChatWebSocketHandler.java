package com.example.whatsapp.socket.ws;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.socket.service.ReceiptEmitterService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionRegistry registry;
    private final KafkaTemplate<String, ChatMessage> chatKafkaTemplate;
    private final ReceiptEmitterService receiptService;
    private final ObjectMapper mapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String user = (String) session.getAttributes().get("user");
        registry.register(user, session);
        log.info("User connected via JWT: {}", user);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String user = (String) session.getAttributes().get("user");
        JsonNode node = mapper.readTree(message.getPayload());

        // 🟢 FIX 1: Null-safe check for the "type" field
        JsonNode typeNode = node.get("type");

        if (typeNode != null && "READ_RECEIPT".equals(typeNode.asText())) {
            receiptService.emitReadReceipt(
                    UUID.fromString(node.get("messageId").asText()),
                    user,
                    node.get("toUser").asText()
            );
            return;
        }

        // 🟢 Normal chat message
        try {
            ChatMessage chat = mapper.treeToValue(node, ChatMessage.class);
            // 🟢 FIX 2: Ensure toUser is present before sending to Kafka
            if (chat != null && chat.toUser() != null) {
                chatKafkaTemplate.send("messages.in", chat.toUser(), chat);
            } else {
                log.warn("Received malformed message from {}: {}", user, message.getPayload());
            }
        } catch (Exception e) {
            log.error("Failed to process message from {}: {}", user, e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String user = (String) session.getAttributes().get("user");
        registry.remove(user);
        log.info("User disconnected: {}", user);
    }
}