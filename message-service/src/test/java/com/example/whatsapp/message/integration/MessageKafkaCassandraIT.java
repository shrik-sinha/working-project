package com.example.whatsapp.message.integration;

import static org.awaitility.Awaitility.await;
import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.repository.ChatMessageRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MessageServiceIT {

    @Autowired
    private KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Autowired
    private ChatMessageRepository repository;

    @Test
    void shouldPersistMessageAndPublishOutboundEvent() {

        // ---------- GIVEN ----------
        ChatMessage msg = new ChatMessage(
                UUID.randomUUID(),
                "alice",
                "bob",
                "hello-from-it-test",
                System.currentTimeMillis()
        );

        // ---------- WHEN ----------
        kafkaTemplate.send("messages.in", "bob", msg);

        // ---------- THEN (Cassandra) ----------
        await().untilAsserted(() -> {
            var rows = repository.findByKeyConversationId("alice#bob");
            assertThat(rows).isNotEmpty();

            ChatMessageEntity saved = rows.get(0);
            assertThat(saved.getPayload()).isEqualTo("hello-from-it-test");
            assertThat(saved.getFromUser()).isEqualTo("alice");
            assertThat(saved.getToUser()).isEqualTo("bob");
        });

        // ---------- THEN (Kafka OUT) ----------
        KafkaConsumer<String, ChatMessage> consumer = outboundConsumer();
        consumer.subscribe(Collections.singletonList("messages.out"));

        ConsumerRecords<String, ChatMessage> records =
                consumer.poll(Duration.ofSeconds(10));

        assertThat(records.isEmpty()).isFalse();

        ConsumerRecord<String, ChatMessage> record =
                records.iterator().next();

        assertThat(record.value().payload())
                .isEqualTo("hello-from-it-test");

        consumer.close();
    }

    private KafkaConsumer<String, ChatMessage> outboundConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "message-service-it");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put("spring.json.trusted.packages", "*");

        return new KafkaConsumer<>(props);
    }
}
