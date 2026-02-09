package com.example.whatsapp.message.consumer;

import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.message.entity.MessageReceiptEntity;
import com.example.whatsapp.message.entity.ReadReceiptEntity;
import com.example.whatsapp.message.entity.ReadReceiptKey;
import com.example.whatsapp.message.repository.MessageReceiptRepository;
import com.example.whatsapp.message.repository.ReadReceiptRepository;
import com.example.whatsapp.message.service.ReceiptPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryReceiptConsumer {

    private final ReceiptPersistenceService receiptPersistenceService;

    @KafkaListener(topics = "receipts")
    public void consume(Receipt receipt) {
        receiptPersistenceService.persist(receipt);
    }
}


