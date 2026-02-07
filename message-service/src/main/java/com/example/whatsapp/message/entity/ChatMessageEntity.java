package com.example.whatsapp.message.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("messages_by_conversation")
public class ChatMessageEntity {

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

    public ChatMessageEntity() {}

    public ConversationMessageKey getKey() {
        return key;
    }

    public void setKey(ConversationMessageKey key) {
        this.key = key;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public String getPayload() {
        return payload;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
