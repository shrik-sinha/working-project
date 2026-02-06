package com.example.whatsapp.socket.ws;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.socket.session.ConnectionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ConnectionRegistry registry;
    private final KafkaTemplate<String, ChatMessage> kafka;

    public ChatWebSocketHandler(
            ConnectionRegistry registry,
            KafkaTemplate<String, ChatMessage> kafka) {
        this.registry = registry;
        this.kafka = kafka;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String user = session.getUri().getQuery().split("=")[1];
        registry.add(user, session);
        System.out.println("USER CONNECTED: " + user);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String user = session.getUri().getQuery().split("=")[1];
        registry.remove(user);
        System.out.println("USER DISCONNECTED: " + user);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage msg)
            throws Exception {

        ChatMessage chat = mapper.readValue(msg.getPayload(), ChatMessage.class);
        kafka.send("messages.in", chat.toUser(), chat);
    }
}
