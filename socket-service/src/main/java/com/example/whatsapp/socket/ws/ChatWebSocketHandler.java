package com.example.whatsapp.socket.ws;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.Envelope;
import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.socket.session.ConnectionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ConnectionRegistry registry;

    // ✅ TWO SEPARATE TEMPLATES (THIS IS THE FIX)
    private final KafkaTemplate<String, ChatMessage> chatKafkaTemplate;
    private final KafkaTemplate<String, Receipt> receiptKafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public ChatWebSocketHandler(
            ConnectionRegistry registry,
            KafkaTemplate<String, ChatMessage> chatKafkaTemplate,
            KafkaTemplate<String, Receipt> receiptKafkaTemplate
    ) {
        this.registry = registry;
        this.chatKafkaTemplate = chatKafkaTemplate;
        this.receiptKafkaTemplate = receiptKafkaTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String user = extractUser(session.getUri());
        registry.register(user, session);
        System.out.println("WS CONNECTED: " + user);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {

        System.out.println("WS RECEIVED RAW: " + message.getPayload());

        Envelope envelope = mapper.readValue(message.getPayload(), Envelope.class);

        switch (envelope.type()) {

            case "CHAT" -> {
                ChatMessage chat =
                        mapper.readValue(envelope.data(), ChatMessage.class);

                System.out.println("WS PUBLISH CHAT TO KAFKA: " + chat);

                chatKafkaTemplate.send(
                        "messages.in",
                        chat.toUser(),
                        chat
                );
            }

            case "RECEIPT" -> {
                Receipt receipt =
                        mapper.readValue(envelope.data(), Receipt.class);

                System.out.println("WS PUBLISH RECEIPT TO KAFKA: " + receipt);

                receiptKafkaTemplate.send(
                        "receipts",
                        receipt.toUser(),
                        receipt
                );
            }

            default -> throw new IllegalArgumentException(
                    "Unknown message type: " + envelope.type()
            );
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.remove(session);
        System.out.println("WS DISCONNECTED");
    }

    private String extractUser(URI uri) {
        // ws://localhost:8081/ws?user=alice
        return uri.getQuery().split("=")[1];
    }
}
