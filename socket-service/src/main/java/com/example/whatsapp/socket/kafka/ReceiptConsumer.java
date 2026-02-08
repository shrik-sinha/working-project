package com.example.whatsapp.socket.kafka;

import com.example.whatsapp.socket.ws.WebSocketSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ReceiptConsumer {

    private final WebSocketSessionRegistry registry;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReceiptConsumer(WebSocketSessionRegistry registry) {
        this.registry = registry;
    }


}
