package com.example.whatsapp.message.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import java.io.Serializable;

@Getter
@Setter
@PrimaryKeyClass
public class ConversationMessageKey implements Serializable {

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
    private long messageTs;

    public ConversationMessageKey(String conversationId, long messageTs) {
        this.conversationId = conversationId;
        this.messageTs = messageTs;
    }
}
