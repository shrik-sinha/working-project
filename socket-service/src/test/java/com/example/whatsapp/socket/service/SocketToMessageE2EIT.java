package com.example.whatsapp.socket.service;

import com.example.whatsapp.common.ChatMessage;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Duration;
import java.util.UUID; // 🔴 This was the missing import
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {
                "spring.profiles.active=test",
                "spring.kafka.consumer.group-id=e2e-test-${random.uuid}",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(
        topics = {"messages.in", "messages.out", "receipts"},
        partitions = 1,
        controlledShutdown = true
)
class SocketToMessageE2EIT {

    @LocalServerPort
    int port;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ConsumerFactory<String, ChatMessage> chatConsumerFactory;

    @Test
    void shouldDeliverAndAckMessage() throws Exception {
        String uniqueContent = "e2e-test-" + UUID.randomUUID();

        // 🔴 Ensure consumer group is also unique to avoid offset pollution
        Consumer<String, ChatMessage> consumer = chatConsumerFactory.createConsumer("e2e-spy-" + UUID.randomUUID(), "a");
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "messages.in");

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketSession alice = client.doHandshake(
                new TextWebSocketHandler() {},
                "ws://localhost:" + port + "/ws?user=alice"
        ).get(5, TimeUnit.SECONDS);

        // Sending the JSON payload with our unique content
        // Change "content" to "payload" to match your handler's expectations
        alice.sendMessage(new TextMessage(String.format(
                "{\"fromUser\":\"alice\",\"toUser\":\"bob\",\"payload\":\"%s\"}", uniqueContent)));

        // Verification using the "Search" method to ignore old messages
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, ChatMessage> records = KafkaTestUtils.getRecords(consumer, Duration.ofMillis(2000));
            boolean found = false;
            for (ConsumerRecord<String, ChatMessage> record : records) {
                // Check payload() or getPayload() based on your ChatMessage DTO
                if (record.value().payload().contains(uniqueContent)) {
                    found = true;
                    break;
                }
            }
            assertThat(found).withFailMessage("Did not find message with unique content: " + uniqueContent).isTrue();
        });

        alice.close();
        consumer.close();
    }
}