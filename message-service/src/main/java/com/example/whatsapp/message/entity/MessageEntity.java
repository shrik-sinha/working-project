package com.example.whatsapp.message.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("messages_by_conversation")
public class MessageEntity {

    @PrimaryKeyColumn(
            name = "conversation_id",
            type = PrimaryKeyType.PARTITIONED
    )
    private String conversationId;

    @PrimaryKeyColumn(
            name = "message_ts",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING
    )
    private Long messageTs;

    @Column("message_id")
    private UUID messageId;

    @Column("from_user")
    private String fromUser;

    @Column("to_user")
    private String toUser;

    @Column("payload")
    private String payload;

    // getters + setters
}
