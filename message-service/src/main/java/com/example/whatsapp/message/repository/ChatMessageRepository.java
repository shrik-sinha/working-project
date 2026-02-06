package com.example.whatsapp.message.repository;

import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.entity.ChatMessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ChatMessageRepository
        extends CassandraRepository<ChatMessageEntity, ChatMessageKey> {
}
