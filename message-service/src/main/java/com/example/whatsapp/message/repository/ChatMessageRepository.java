package com.example.whatsapp.message.repository;

import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.entity.ConversationMessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository
        extends CassandraRepository<ChatMessageEntity, ConversationMessageKey> {

    List<ChatMessageEntity> findByKeyConversationId(String conversationId);
}
