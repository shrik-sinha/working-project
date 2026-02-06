package com.example.whatsapp.message.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;

@Table("messages_by_conversation")
public class ChatMessageEntity {

    @PrimaryKey
    private ChatMessageKey key;

    @Column("message_id")
    private String messageId;

    @Column("from_user")
    private String fromUser;

    @Column("to_user")
    private String toUser;

    private String payload;

    public ChatMessageKey getKey() {
        return key;
    }

    public void setKey(ChatMessageKey key) {
        this.key = key;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
