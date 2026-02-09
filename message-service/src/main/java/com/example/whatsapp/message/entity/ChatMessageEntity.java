package com.example.whatsapp.message.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("messages_by_conversation")
public class ChatMessageEntity {

    @Setter
    @PrimaryKey
    private ConversationMessageKey key;

    @Column("message_id")
    private String messageId;

    @Column("from_user")
    private String fromUser;

    @Column("to_user")
    private String toUser;

    @Column("payload")
    private String payload;

    @Column("status")
    private String status; // SENT, DELIVERED, SEEN

}
