package com.example.whatsapp.message.processor;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.entity.ConversationMessageKey;
import com.example.whatsapp.message.repository.ChatMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.whatsapp.message.util.ConversationUtil.conversationId;

@Slf4j
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
            Acknowledgment ack
    ) {

        try {
            UUID messageId =
                    message.messageId() != null
                            ? message.messageId()
                            : UUID.randomUUID();

            ChatMessage enriched = new ChatMessage(
                    messageId,
                    message.fromUser(),
                    message.toUser(),
                    message.payload(),
                    message.timestamp() > 0
                            ? message.timestamp()
                            : System.currentTimeMillis()
            );

            log.info("MESSAGE SERVICE RECEIVED CHAT: {}", enriched);

            ConversationMessageKey key = new ConversationMessageKey(
                    conversationId(enriched.fromUser(), enriched.toUser()),
                    enriched.timestamp()
            );

            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setKey(key);
            entity.setMessageId(messageId);
            entity.setFromUser(enriched.fromUser());
            entity.setToUser(enriched.toUser());
            entity.setPayload(enriched.payload());
            entity.setStatus(MessageStatus.SENT.toString());

            // 1️⃣ Persist
            repository.save(entity);

            // 2️⃣ Publish ENRICHED message
            kafkaTemplate.send(
                    "messages.out",
                    enriched.toUser(),
                    enriched
            );

            // 3️⃣ Ack
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process message", e);
            throw e;
        }
    }

}

