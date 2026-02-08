package com.example.whatsapp.message.processor;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.entity.ConversationMessageKey;
import com.example.whatsapp.message.repository.ChatMessageRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageProcessor {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    private final ChatMessageRepository repository;

    public MessageProcessor(
            KafkaTemplate<String, ChatMessage> kafkaTemplate,
            ChatMessageRepository repository
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.repository = repository;
    }

    @KafkaListener(
            topics = "messages.in",
            containerFactory = "chatKafkaListenerContainerFactory"
    )
    public void processMessage(
            ChatMessage message,
            org.springframework.kafka.support.Acknowledgment ack
    ) {

        try {
            System.out.println("MESSAGE SERVICE RECEIVED CHAT: " + message);

            ConversationMessageKey key = new ConversationMessageKey(
                    conversationId(message.fromUser(), message.toUser()),
                    message.timestamp()
            );

            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setKey(key);
            entity.setMessageId(
                    message.messageId() != null
                            ? message.messageId().toString()
                            : UUID.randomUUID().toString()
            );
            entity.setFromUser(message.fromUser());
            entity.setToUser(message.toUser());
            entity.setPayload(message.payload());

            // 1️⃣ Persist to Cassandra
            repository.save(entity);

            // 2️⃣ Publish downstream
            kafkaTemplate.send(
                    "messages.out",
                    message.toUser(),
                    message
            );

            // 3️⃣ Commit Kafka offset ONLY after success
            ack.acknowledge();

        } catch (Exception e) {
            // ❌ DO NOT ACK
            // Kafka will retry the message
            System.err.println("Failed to process message: " + message);
            throw e;
        }
    }

    private static String conversationId(String u1, String u2) {
        return u1.compareTo(u2) < 0
                ? u1 + "#" + u2
                : u2 + "#" + u1;
    }
}

