package com.example.whatsapp.message;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.entity.ChatMessageKey;
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
    public void processMessage(ChatMessage message) {

        System.out.println("MESSAGE SERVICE RECEIVED CHAT: " + message);

        // 🔹 Build Cassandra key
        ChatMessageKey key = new ChatMessageKey();
        key.setConversationId(conversationId(
                message.fromUser(),
                message.toUser()
        ));
        key.setMessageTs(message.timestamp());

        // 🔹 Build entity
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

        // 🔹 Save to Cassandra
        repository.save(entity);

        // 🔹 Publish to socket-service
        kafkaTemplate.send(
                "messages.out",
                message.toUser(),
                message
        );
    }

    private String conversationId(String u1, String u2) {
        return (u1.compareTo(u2) < 0)
                ? u1 + "#" + u2
                : u2 + "#" + u1;
    }
}
