package com.example.whatsapp.message.entity;

import com.example.whatsapp.common.Receipt;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Setter
@Getter
@Table("message_receipts")
public class MessageReceiptEntity {

    @PrimaryKey
    private MessageReceiptKey key;

    @Column("from_user")
    private String fromUser;

    @Column("to_user")
    private String toUser;

    @Column("status")
    private String status;

    public MessageReceiptEntity() {}

    public static MessageReceiptEntity from(Receipt receipt) {
        MessageReceiptEntity entity = new MessageReceiptEntity();
        entity.setKey(
                new MessageReceiptKey(
                        receipt.messageId(),
                        receipt.timestamp()
                )
        );
        entity.setFromUser(receipt.fromUser());
        entity.setToUser(receipt.toUser());
        entity.setStatus(receipt.status().name());
        return entity;
    }


}
