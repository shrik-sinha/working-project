package com.example.whatsapp.socket.kafka;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.socket.session.ConnectionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class OutboundMessageConsumer {

    private final ConnectionRegistry registry;
    private final ObjectMapper mapper = new ObjectMapper();

    public OutboundMessageConsumer(ConnectionRegistry registry) {
        this.registry = registry;
    }

    @KafkaListener(
            topics = "messages.out",
            containerFactory = "chatKafkaListenerContainerFactory"
    )
    public void consume(ChatMessage msg) throws Exception {

        System.out.println("SOCKET RECEIVED FROM KAFKA: " + msg);

        WebSocketSession session = registry.get(msg.toUser());
        if (session != null && session.isOpen()) {
            session.sendMessage(
                    new TextMessage(mapper.writeValueAsString(msg))
            );
        }
    }
}
