package com.example.whatsapp.message.api;

import com.example.whatsapp.common.ChatMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public MessageController(KafkaTemplate<String, ChatMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody ChatMessage message) {

        ChatMessage enriched = new ChatMessage(
                message.messageId() != null ? message.messageId() : UUID.randomUUID(),
                message.fromUser(),
                message.toUser(),
                message.payload(),
                System.currentTimeMillis()
        );

        kafkaTemplate.send(
                "messages.in",
                enriched.toUser(),
                enriched
        );

        return "Message published to Kafka";
    }
}

