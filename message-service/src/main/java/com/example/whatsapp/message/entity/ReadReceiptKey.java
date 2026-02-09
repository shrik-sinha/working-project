package com.example.whatsapp.message.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

@PrimaryKeyClass
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadReceiptKey implements Serializable {

    @PrimaryKeyColumn(
            name = "message_id",
            type = PrimaryKeyType.PARTITIONED
    )
    private UUID messageId;

    @PrimaryKeyColumn(
            name = "reader",
            type = PrimaryKeyType.CLUSTERED
    )
    private String reader;
}


