package com.example.whatsapp.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @Test
    void shouldCreateChatMessage() {
        ChatMessage msg = new ChatMessage(
                null,
                "alice",
                "bob",
                "hello",
                System.currentTimeMillis()
        );

        assertThat(msg.fromUser()).isEqualTo("alice");
        assertThat(msg.toUser()).isEqualTo("bob");
        assertThat(msg.payload()).isEqualTo("hello");
    }
}
