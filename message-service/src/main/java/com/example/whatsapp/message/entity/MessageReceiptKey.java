package com.example.whatsapp.message.entity;

import lombok.Getter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

@Getter
@PrimaryKeyClass
public class MessageReceiptKey implements Serializable {

    @PrimaryKeyColumn(
            name = "message_id",
            type = PrimaryKeyType.PARTITIONED
    )
    private UUID messageId;

    @PrimaryKeyColumn(
            name = "event_ts",
            type = PrimaryKeyType.CLUSTERED
    )
    private long eventTs;

    public MessageReceiptKey(UUID messageId, long eventTs) {
        this.messageId = messageId;
        this.eventTs = eventTs;
    }

}
