package com.example.whatsapp.socket.service;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.socket.ws.WebSocketSessionRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        log.info("SOCKET SERVICE RECEIVED FROM KAFKA: {}", message);

        sessionRegistry.sendToUser(
                message.toUser(),
                message
        );
    }
}
