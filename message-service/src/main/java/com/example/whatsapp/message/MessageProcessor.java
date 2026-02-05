package com.example.whatsapp.message;

import com.example.whatsapp.common.ChatMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public MessageProcessor(KafkaTemplate<String, ChatMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "messages.in",
            containerFactory = "chatKafkaListenerContainerFactory"
    )
    public void processMessage(ChatMessage message) {

        System.out.println("MESSAGE SERVICE RECEIVED CHAT: " + message);

        kafkaTemplate.send(
                "messages.out",
                message.toUser(),   // record accessor (records have methods)
                message
        );
    }
}


