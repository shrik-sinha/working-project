package com.example.whatsapp.socket.service;

import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.common.Receipt;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptEmitterService {

    private final KafkaTemplate<String, Receipt> receiptKafkaTemplate;

    public void emitReadReceipt(UUID messageId, String reader, String sender) {
        Receipt receipt = new Receipt(
                messageId,
                reader,
                sender,
                MessageStatus.READ,
                System.currentTimeMillis()
        );
        receiptKafkaTemplate.send("receipts", sender, receipt);
    }

}
