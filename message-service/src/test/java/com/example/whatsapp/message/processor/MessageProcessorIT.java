package com.example.whatsapp.message.processor;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.message.repository.ChatMessageRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
        topics = {"messages.in", "messages.out"},
        partitions = 1
)
class MessageProcessorIT {

    @Autowired
    KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Autowired
    ChatMessageRepository repository;

    @Test
    void shouldPersistAndForwardMessage() {
        ChatMessage msg = new ChatMessage(
                UUID.randomUUID(),
                "alice",
                "bob",
                "hello",
                System.currentTimeMillis()
        );

        kafkaTemplate.send("messages.in", "bob", msg);

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(repository.findAll()).isNotEmpty()
                );
    }
}

