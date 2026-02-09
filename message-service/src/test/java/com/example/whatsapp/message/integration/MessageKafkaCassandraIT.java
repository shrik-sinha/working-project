package com.example.whatsapp.message.integration;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.repository.ChatMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
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
                "hello-from-it-test1",
                System.currentTimeMillis()
        );

        // ---------- WHEN ----------
        log.info("MESSAGE SENT VIA TESTING from MessageServiceIT class to Kafka Topic - messages.in: {}", msg);

        kafkaTemplate.send("messages.in", "bob", msg);

        // ---------- THEN (Cassandra) ----------
        await().untilAsserted(() -> {
            var rows = repository.findByKeyConversationId("alice#bob");
            repository.findByKeyConversationId("alice#bob").forEach(e -> log.info("Record = {}", e));

            assertThat(rows).isNotEmpty();

            ChatMessageEntity saved = rows.get(0);
            assertThat(saved.getPayload()).isEqualTo("hello-from-it-test1");
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
                .isEqualTo("hello-from-it-test1");

        consumer.close();
    }

    private KafkaConsumer<String, ChatMessage> outboundConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "message-service-it");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);

        props.put(JsonDeserializer.TRUSTED_PACKAGES,
                "com.example.whatsapp");

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
                "com.example.whatsapp.common.ChatMessage");

        return new KafkaConsumer<>(props);
    }
}
