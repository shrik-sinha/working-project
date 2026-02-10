package com.example.whatsapp.message.repository;

import com.example.whatsapp.message.entity.MessageReceiptEntity;
import com.example.whatsapp.message.entity.MessageReceiptKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReceiptRepository
        extends CassandraRepository<MessageReceiptEntity, MessageReceiptKey> {
}

