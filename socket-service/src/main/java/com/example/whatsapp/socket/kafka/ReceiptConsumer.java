package com.example.whatsapp.socket.kafka;

import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.socket.session.ConnectionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
public class ReceiptConsumer {

    private final ConnectionRegistry registry;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReceiptConsumer(ConnectionRegistry registry) {
        this.registry = registry;
    }

    /*@KafkaListener(
            topics = "receipts",
            containerFactory = "receiptKafkaListenerContainerFactory"
    )*/    public void consume(Receipt receipt) throws Exception {
        var session = registry.get(receipt.toUser());
        if (session != null && session.isOpen()) {
            session.sendMessage(
                    new TextMessage(mapper.writeValueAsString(receipt))
            );
        }
    }
}
