package com.example.whatsapp.socket.ws;

import com.example.whatsapp.common.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for chat messages.
 *
 * Expected connection URL format:
 *   ws://localhost:8081/ws?user=alice
 *
 * Security note: This is still using query parameter → should be replaced with
 * proper authentication (JWT in subprotocol / cookie / header) in production.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionRegistry registry;
    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Cache user → session locally as well → faster lookup + helps detect stale sessions
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String user = extractUser(session);

        if (user == null || user.trim().isEmpty()) {
            log.warn("Connection attempt without valid user parameter → closing");
            session.close(CloseStatus.BAD_DATA.withReason("Missing or invalid user parameter"));
            return;
        }

        // Check if user is already connected (same user multiple tabs/devices)
        WebSocketSession existing = sessions.putIfAbsent(user, session);
        if (existing != null && existing.isOpen()) {
            log.info("User {} already connected. Closing old session.", user);
            try {
                existing.close(CloseStatus.NORMAL.withReason("New connection from same user"));
            } catch (Exception e) {
                // ignore
            }
        }

        registry.register(user, session);
        sessions.put(user, session);

        log.info("User connected: {} | Session: {}", user, session.getId());

        // Optional: send welcome / last seen messages / pending messages here
        // sendPendingMessages(user, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String user = extractUser(session);
        if (user == null) {
            session.close(CloseStatus.BAD_DATA.withReason("User identity lost"));
            return;
        }

        String payload = message.getPayload();
        log.debug("Received from {}: {}", user, payload.length() > 120 ? payload.substring(0, 120) + "..." : payload);

        try {
            ChatMessage chat = objectMapper.readValue(payload, ChatMessage.class);

            // Basic validation
            if (chat.fromUser() == null || !chat.fromUser().equals(user)) {
                log.warn("User {} sent message with mismatched fromUser: {}", user, chat.fromUser());
                sendError(session, "fromUser must match authenticated user");
                return;
            }

            if (chat.toUser() == null || chat.toUser().trim().isEmpty()) {
                sendError(session, "toUser is required");
                return;
            }

            // Optional: enrich message before sending to Kafka
            // ChatMessage enriched = enrichWithMetadata(chat, session);

            kafkaTemplate.send("messages.in", chat.toUser(), chat)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish message to Kafka", ex);
                        } else {
                            log.debug("Message published to Kafka → topic: messages.in, key: {}", chat.toUser());
                        }
                    });

        } catch (Exception e) {
            log.warn("Invalid message format from user {}: {}", user, e.toString());
            sendError(session, "Invalid message format");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String user = extractUser(session);
        if (user != null) {
            registry.remove(user);
            sessions.remove(user);
            log.info("User disconnected: {} | reason: {}", user, status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String user = extractUser(session);
        log.warn("Transport error for user {}: {}", user, exception.toString());
        // You may decide to close here or give some grace period
    }

    // ────────────────────────────────────────────────
    //  Helpers
    // ────────────────────────────────────────────────

    private String extractUser(WebSocketSession session) {
        try {
            String query = session.getUri().getQuery();
            if (query == null || !query.contains("user=")) {
                return null;
            }
            String[] parts = query.split("[=&]");
            for (int i = 0; i < parts.length - 1; i++) {
                if ("user".equals(parts[i])) {
                    return parts[i + 1];
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void sendError(WebSocketSession session, String errorMsg) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(
                    "{\"type\":\"error\",\"message\":\"" + errorMsg + "\"}"
            ));
        }
    }

    // Example: future placeholder
    /*
    private void sendPendingMessages(String user, WebSocketSession session) {
        // You can query Cassandra or Redis for undelivered messages
        // and push them here after connection is established
    }
    */
}