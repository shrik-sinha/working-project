package com.example.whatsapp.socket.kafka;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.socket.ws.WebSocketSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class OutboundMessageConsumer {

    private final WebSocketSessionRegistry registry;
    private final KafkaTemplate<String, Receipt> receiptKafka;
    private final ObjectMapper mapper = new ObjectMapper();

    public OutboundMessageConsumer(
            WebSocketSessionRegistry registry,
            KafkaTemplate<String, Receipt> receiptKafka
    ) {
        this.registry = registry;
        this.receiptKafka = receiptKafka;
    }

    @KafkaListener(
            topics = "messages.out",
            containerFactory = "chatKafkaListenerContainerFactory"
    )
    public void consume(ChatMessage msg) throws Exception {

        WebSocketSession session = registry.getSessionByUser(msg.toUser());

        if (session != null && session.isOpen()) {
            session.sendMessage(
                    new TextMessage(mapper.writeValueAsString(msg))
            );

            // 🔥 DELIVERY ACK
            Receipt delivered = new Receipt(
                    msg.messageId(),
                    msg.toUser(),
                    msg.fromUser(),
                    MessageStatus.DELIVERED,
                    System.currentTimeMillis()
            );

            receiptKafka.send(
                    "receipts.delivery",
                    msg.fromUser(),
                    delivered
            );
        }
    }
}
