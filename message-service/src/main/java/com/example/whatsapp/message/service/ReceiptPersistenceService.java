package com.example.whatsapp.message.service;

import com.example.whatsapp.common.MessageStatus;
import com.example.whatsapp.common.Receipt;
import com.example.whatsapp.message.entity.MessageReceiptEntity;
import com.example.whatsapp.message.entity.MessageReceiptKey;
import com.example.whatsapp.message.entity.ReadReceiptEntity;
import com.example.whatsapp.message.entity.ReadReceiptKey;
import com.example.whatsapp.message.repository.MessageReceiptRepository;
import com.example.whatsapp.message.repository.ReadReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReceiptPersistenceService {

    private final MessageReceiptRepository eventRepo;
    private final ReadReceiptRepository readRepo;

    public void persist(Receipt receipt) {

        // 1️⃣ Always persist event (append-only)
        MessageReceiptEntity event = new MessageReceiptEntity();
        event.setKey(new MessageReceiptKey(
                receipt.messageId(),
                receipt.timestamp()
        ));
        event.setFromUser(receipt.fromUser());
        event.setToUser(receipt.toUser());
        event.setStatus(receipt.status().name());

        eventRepo.save(event);

        // 2️⃣ READ idempotency table
        if (receipt.status() == MessageStatus.READ) {
            ReadReceiptKey key =
                    new ReadReceiptKey(receipt.messageId(), receipt.fromUser());

            if (!readRepo.existsByKeyMessageIdAndKeyReader(
                    receipt.messageId(),
                    receipt.fromUser()
            )) {
                readRepo.save(new ReadReceiptEntity(key, receipt.timestamp()));
            }
        }
    }
}




