package com.example.whatsapp.socket.service;

import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.common.Receipt;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ReceiptEmitterServiceTest {

    @Autowired
    ReceiptEmitterService service;

    @MockBean
    KafkaTemplate<String, Receipt> kafkaTemplate;

    @Test
    void shouldEmitReadReceipt() {
        service.emitReadReceipt(
                UUID.randomUUID(),
                "bob",
                "alice"
        );

        verify(kafkaTemplate).send(
                eq("receipts"),
                eq("alice"),
                any(Receipt.class)
        );
    }
}

