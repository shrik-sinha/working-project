package com.example.whatsapp.common;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiptTest {

    @Test
    void shouldCreateReadReceipt() {
        Receipt r = new Receipt(
                UUID.randomUUID(),
                "bob",
                "alice",
                MessageStatus.READ,
                System.currentTimeMillis()
        );

        assertThat(r.status()).isEqualTo(MessageStatus.READ);
    }
}
