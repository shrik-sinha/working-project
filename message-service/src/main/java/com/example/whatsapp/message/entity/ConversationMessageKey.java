package com.example.whatsapp.message.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import java.io.Serializable;

@PrimaryKeyClass
public class ConversationMessageKey implements Serializable {

    @PrimaryKeyColumn(
            name = "conversation_id",
            type = PrimaryKeyType.PARTITIONED
    )
    private String conversationId;

    @PrimaryKeyColumn(
            name = "message_ts",
            type = PrimaryKeyType.CLUSTERED
    )
    private long messageTs;

    public ConversationMessageKey() {}

    public ConversationMessageKey(String conversationId, long messageTs) {
        this.conversationId = conversationId;
        this.messageTs = messageTs;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public long getMessageTs() {
        return messageTs;
    }

    public void setMessageTs(long messageTs) {
        this.messageTs = messageTs;
    }
}
