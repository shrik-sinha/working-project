package com.example.whatsapp.socket.service;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.socket.ws.WebSocketSessionRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OutgoingMessageListener {

    private final WebSocketSessionRegistry sessionRegistry;

    public OutgoingMessageListener(WebSocketSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @KafkaListener(
            topics = "messages.out",
            containerFactory = "chatKafkaListenerContainerFactory"
    )
    public void onMessage(ChatMessage message) {

        System.out.println("SOCKET SERVICE RECEIVED FROM KAFKA: " + message);

        sessionRegistry.sendToUser(
                message.toUser(),
                message
        );
    }
}
