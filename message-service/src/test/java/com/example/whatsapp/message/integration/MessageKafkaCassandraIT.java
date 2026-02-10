package com.example.whatsapp.message.integration;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.repository.ChatMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(topics = {"messages.in", "messages.out"}, partitions = 1, controlledShutdown = true)
class MessageServiceIT {

    @Autowired
    private KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Autowired
    private ChatMessageRepository repository;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ConsumerFactory<String, ChatMessage> chatConsumerFactory;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldPersistMessageAndPublishOutboundEvent() {
        String uniqueContent = "hello-it-" + UUID.randomUUID();
        ChatMessage msg = new ChatMessage(UUID.randomUUID(), "alice", "bob", uniqueContent, System.currentTimeMillis());

        // 1. Setup Spy Consumer for outbound topic
        Consumer<String, ChatMessage> consumer = chatConsumerFactory.createConsumer("test-group-" + UUID.randomUUID(), "a");
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "messages.out");

        // 2. SEND
        kafkaTemplate.send("messages.in", "bob", msg);

        // 3. ASSERT DB (Cassandra/H2)
        await().untilAsserted(() -> {
            var rows = repository.findByKeyConversationId("alice#bob");
            assertThat(rows).isNotEmpty();
            assertThat(rows.get(0).getPayload()).isEqualTo(uniqueContent);
        });

        // 4. ASSERT Kafka Outbound
// Poll for all available records instead of expecting just one
        ConsumerRecords<String, ChatMessage> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

        assertThat(records).isNotEmpty();

// Look through all received records for the one containing our uniqueContent
        boolean foundOurMessage = false;
        for (ConsumerRecord<String, ChatMessage> record : records) {
            if (record.value().payload().equals(uniqueContent)) {
                foundOurMessage = true;
                break;
            }
        }

        assertThat(foundOurMessage)
                .withFailMessage("Expected to find a Kafka message with payload: " + uniqueContent)
                .isTrue();
        consumer.close();
    }
}