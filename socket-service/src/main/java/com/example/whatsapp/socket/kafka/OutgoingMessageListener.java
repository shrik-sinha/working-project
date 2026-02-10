package com.example.whatsapp.socket.kafka;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.socket.ws.WebSocketSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class OutgoingMessageListener {

    private final WebSocketSessionRegistry sessionRegistry;
    private final KafkaTemplate<String, Receipt> receiptKafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutgoingMessageListener(
            WebSocketSessionRegistry sessionRegistry,
            KafkaTemplate<String, Receipt> receiptKafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.sessionRegistry = sessionRegistry;
        this.receiptKafkaTemplate = receiptKafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /* =====================================================
       Deliver message to receiver + emit DELIVERY receipt
       ===================================================== */

    @KafkaListener(
            topics = "messages.out",
            containerFactory = "chatKafkaListenerContainerFactory"
    )
    public void consume(ChatMessage msg) throws Exception {

        log.info("‼️ KAFKA CONSUMED MESSAGE: {}", msg);

        WebSocketSession session =
                sessionRegistry.getSessionByUser(msg.toUser());

        if (session != null && session.isOpen()) {

            // 1️⃣ Deliver message to receiver
            session.sendMessage(
                    new TextMessage(objectMapper.writeValueAsString(msg))
            );

            log.info("Delivered message {} to user {}",
                    msg.messageId(), msg.toUser());

            // 2️⃣ Emit DELIVERY receipt
            Receipt receipt = new Receipt(
                    msg.messageId(),
                    msg.toUser(),     // receiver
                    msg.fromUser(),   // sender
                    MessageStatus.DELIVERED,
                    System.currentTimeMillis()
            );

            receiptKafkaTemplate.send(
                    "receipts",
                    receipt.toUser(),
                    receipt
            );
        }
    }

    /* =====================================================
       Push DELIVERY receipt back to sender (✓✓)
       ===================================================== */

    @KafkaListener(
            topics = "receipts",
            containerFactory = "receiptKafkaListenerContainerFactory"
    )
    public void pushReceiptToSender(Receipt receipt) {

        if (receipt.status() == MessageStatus.DELIVERED) {
            sessionRegistry.sendToUser(
                    receipt.toUser(), // sender
                    receipt
            );

            log.info("Sent DELIVERY receipt for message {} to sender {}",
                    receipt.messageId(), receipt.toUser());
        }
    }
}
