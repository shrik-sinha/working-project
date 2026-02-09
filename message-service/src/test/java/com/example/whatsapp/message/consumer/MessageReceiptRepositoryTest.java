package com.example.whatsapp.message.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.message.repository.ReadReceiptRepository;
import com.example.whatsapp.message.service.ReceiptPersistenceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class DeliveryReceiptConsumerTest {

    @Autowired
    ReceiptPersistenceService service;

    @Autowired
    ReadReceiptRepository readRepo;

    @Test
    void shouldPersistReadReceiptIdempotently() {

        UUID messageId = UUID.randomUUID();

        Receipt receipt = new Receipt(
                messageId,
                "bob",
                "alice",
                MessageStatus.READ,
                System.currentTimeMillis()
        );

        service.persist(receipt);
        service.persist(receipt); // duplicate

        Assertions.assertTrue(readRepo.existsByKeyMessageIdAndKeyReader(messageId, "bob"));
    }
}

