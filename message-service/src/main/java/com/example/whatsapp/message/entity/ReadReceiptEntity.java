package com.example.whatsapp.message.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.*;

@Table("read_receipts_by_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptEntity {

    @PrimaryKey
    private ReadReceiptKey key;

    @Column("read_ts")
    private long readTs;
}


