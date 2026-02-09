package com.example.whatsapp.socket.integration;

import com.example.whatsapp.common.ChatMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.KafkaTemplate;
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

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test"
)
class SocketServiceIT {

    @LocalServerPort
    int port;


    @Autowired
    KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Test
    void shouldDeliverMessageOverWebSocket() throws Exception {

        BlockingQueue<String> received = new ArrayBlockingQueue<>(1);

        StandardWebSocketClient client = new StandardWebSocketClient();

        WebSocketSession session = client.execute(
                new AbstractWebSocketHandler() {
                    @Override
                    protected void handleTextMessage(
                            @NonNull WebSocketSession session,
                            @NonNull TextMessage message
                    ) {
                        received.offer(message.getPayload());
                    }
                },
                null,
                URI.create("ws://localhost:" + port + "/ws?user=bob")
        ).get();

        // ---------- WHEN ----------
        ChatMessage msg = new ChatMessage(
                UUID.randomUUID(),
                "alice",
                "bob",
                "hello-via-kafka",
                System.currentTimeMillis()
        );

        kafkaTemplate.send("messages.out", "bob", msg);

        // ---------- THEN ----------
        String payload = received.poll(10, TimeUnit.SECONDS);

        assertThat(payload).isNotNull();
        assertThat(payload).contains("hello-via-kafka");

        session.close();
    }
}

