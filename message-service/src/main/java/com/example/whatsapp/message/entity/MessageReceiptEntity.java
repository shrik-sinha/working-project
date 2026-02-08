package com.example.whatsapp.message.entity;

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

}
