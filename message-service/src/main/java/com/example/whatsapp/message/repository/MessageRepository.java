package com.example.whatsapp.message.repository;

import com.example.whatsapp.message.entity.MessageEntity;
import com.example.whatsapp.message.entity.MessagePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface MessageRepository
        extends CassandraRepository<MessageEntity, MessagePrimaryKey> {
}
