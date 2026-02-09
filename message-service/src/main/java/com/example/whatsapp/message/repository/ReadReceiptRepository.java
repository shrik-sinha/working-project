package com.example.whatsapp.message.repository;

import com.example.whatsapp.message.entity.ReadReceiptEntity;
import com.example.whatsapp.message.entity.ReadReceiptKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReadReceiptRepository
        extends CassandraRepository<ReadReceiptEntity, ReadReceiptKey> {

    boolean existsByKeyMessageIdAndKeyReader(UUID messageId, String reader);
}





