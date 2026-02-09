package com.example.whatsapp.message.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@ToString
@Table("messages_by_conversation")
public class ChatMessageEntity {

    @Setter
    @PrimaryKey
    private ConversationMessageKey key;

    @Column("message_id")
    private UUID messageId;

    @Column("from_user")
    private String fromUser;

    @Column("to_user")
    private String toUser;

    @Column("payload")
    private String payload;

    @Column("status")
    private String status; // SENT, DELIVERED, SEEN

}
