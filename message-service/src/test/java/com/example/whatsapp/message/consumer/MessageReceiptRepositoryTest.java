package com.example.whatsapp.message.consumer;

import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.message.repository.ReadReceiptRepository;
import com.example.whatsapp.message.service.ReceiptPersistenceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

@SpringBootTest
@DirtiesContext // Ensures the repository check is clean for other tests
class DeliveryReceiptConsumerTest {

    @Autowired
    ReceiptPersistenceService service;

    @Autowired
    ReadReceiptRepository readRepo;

    @Test
    void shouldPersistReadReceiptIdempotently() {
        UUID messageId = UUID.randomUUID();
        Receipt receipt = new Receipt(messageId, "bob", "alice", MessageStatus.READ, System.currentTimeMillis());

        service.persist(receipt);
        service.persist(receipt); // duplicate call

        Assertions.assertTrue(readRepo.existsByKeyMessageIdAndKeyReader(messageId, "bob"));
    }
}