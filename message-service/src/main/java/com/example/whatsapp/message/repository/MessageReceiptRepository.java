package com.example.whatsapp.message.repository;

import com.example.whatsapp.message.entity.MessageReceiptEntity;
import com.example.whatsapp.message.entity.MessageReceiptKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface MessageReceiptRepository
        extends CassandraRepository<MessageReceiptEntity, MessageReceiptKey> {
}
