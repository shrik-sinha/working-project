package com.example.whatsapp.message.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

@PrimaryKeyClass
public class ChatMessageKey {

    @PrimaryKeyColumn(
            name = "conversation_id",
            type = PrimaryKeyType.PARTITIONED
    )
    private String conversationId;

    @PrimaryKeyColumn(
            name = "message_ts",
            type = PrimaryKeyType.CLUSTERED
    )
    private Long messageTs;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Long getMessageTs() {
        return messageTs;
    }

    public void setMessageTs(Long messageTs) {
        this.messageTs = messageTs;
    }
}
