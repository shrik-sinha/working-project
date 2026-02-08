package com.example.whatsapp.message.processor;

import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.message.entity.ConversationMessageKey;
import com.example.whatsapp.message.repository.ChatMessageRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.example.whatsapp.message.util.ConversationUtil.conversationId;

@Component
public class DeliveryReceiptConsumer {

    private final ChatMessageRepository repository;

    public DeliveryReceiptConsumer(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "receipts.delivery",
            containerFactory = "receiptKafkaListenerContainerFactory"
    )
    public void consume(Receipt receipt) {

        repository.findById(repositoryKey(receipt)
        ).ifPresent(entity -> {
            entity.setStatus(MessageStatus.DELIVERED.name());
            repository.save(entity);
        });
    }

    private ConversationMessageKey repositoryKey(Receipt r) {
        return new ConversationMessageKey(
                conversationId(r.fromUser(), r.toUser()),
                r.timestamp()
        );
    }
}
