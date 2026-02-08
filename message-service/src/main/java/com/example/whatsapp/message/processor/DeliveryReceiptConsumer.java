package com.example.whatsapp.message.processor;

import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.message.entity.MessageReceiptEntity;
import com.example.whatsapp.message.entity.MessageReceiptKey;
import com.example.whatsapp.message.repository.MessageReceiptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeliveryReceiptConsumer {

    private final MessageReceiptRepository repository;

    public DeliveryReceiptConsumer(MessageReceiptRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "receipts",
            containerFactory = "receiptKafkaListenerContainerFactory"
    )
    public void persistReceipt(Receipt receipt) {

        MessageReceiptKey key =
                new MessageReceiptKey(
                        receipt.messageId(),
                        receipt.timestamp()
                );

        MessageReceiptEntity entity = new MessageReceiptEntity();
        entity.setKey(key);
        entity.setFromUser(receipt.fromUser());
        entity.setToUser(receipt.toUser());
        entity.setStatus(receipt.status().name());

        repository.save(entity);

        log.info("Receipt persisted: {}", receipt);
    }
}
