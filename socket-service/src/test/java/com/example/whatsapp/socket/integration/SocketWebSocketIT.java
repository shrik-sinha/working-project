package com.example.whatsapp.socket.integration;

import com.example.whatsapp.common.ChatMessage;
import org.awaitility.Awaitility;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.profiles.active=test",
                "spring.kafka.consumer.group-id=socket-integration-test-${random.uuid}",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}" // 🔴 CRITICAL
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(
        topics = {"messages.out"},
        partitions = 1,
        controlledShutdown = true
)
class SocketServiceIT {

    @LocalServerPort
    int port;

    @Autowired
    KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Test
    void shouldDeliverMessageOverWebSocket() throws Exception {
        BlockingQueue<String> received = new ArrayBlockingQueue<>(50); // Larger buffer
        StandardWebSocketClient client = new StandardWebSocketClient();

        WebSocketSession session = client.execute(
                new AbstractWebSocketHandler() {
                    @Override
                    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
                        received.offer(message.getPayload());
                    }
                },
                null,
                URI.create("ws://localhost:" + port + "/ws?user=bob")
        ).get(5, TimeUnit.SECONDS);

        String uniquePayload = "socket-unique-" + UUID.randomUUID();
        ChatMessage msg = new ChatMessage(UUID.randomUUID(), "alice", "bob", uniquePayload, System.currentTimeMillis());

        kafkaTemplate.send("messages.out", "bob", msg);

        // 🔴 FIX: Search the queue for our specific payload, ignoring old messages
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            boolean matchFound = received.stream().anyMatch(content -> content.contains(uniquePayload));
            assertThat(matchFound)
                    .withFailMessage("WebSocket did not receive unique payload: " + uniquePayload)
                    .isTrue();
        });

        session.close();
    }
}