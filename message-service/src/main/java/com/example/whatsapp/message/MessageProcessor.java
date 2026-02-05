package com.example.whatsapp.message;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.Receipt;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProcessor {

    private final KafkaTemplate<String, ChatMessage> chatKafkaTemplate;
    private final KafkaTemplate<String, Receipt> receiptKafkaTemplate;

    public MessageProcessor(
            KafkaTemplate<String, ChatMessage> chatKafkaTemplate,
            KafkaTemplate<String, Receipt> receiptKafkaTemplate) {
        this.chatKafkaTemplate = chatKafkaTemplate;
        this.receiptKafkaTemplate = receiptKafkaTemplate;
    }

    /* ========== CHAT FLOW ========== */

    @KafkaListener(
            topics = "messages.in",
            containerFactory = "chatKafkaListenerContainerFactory"
    )
    public void processMessage(ChatMessage msg) {
        System.out.println("MESSAGE SERVICE RECEIVED CHAT: " + msg);
        chatKafkaTemplate.send("messages.out", msg.toUser(), msg);
    }

    /* ========== RECEIPT FLOW ========== */

    @KafkaListener(
            topics = "receipts.in",
            containerFactory = "receiptKafkaListenerContainerFactory"
    )
    public void processReceipt(Receipt receipt) {
        System.out.println("MESSAGE SERVICE RECEIVED RECEIPT: " + receipt);
        receiptKafkaTemplate.send("receipts.out", receipt.toUser(), receipt);
    }
}

