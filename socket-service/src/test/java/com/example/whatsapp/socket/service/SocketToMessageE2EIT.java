package com.example.whatsapp.socket.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EmbeddedKafka(
        topics = {"messages.in", "messages.out", "receipts"}
)
class SocketToMessageE2EIT {

    @LocalServerPort
    int port;

    @Test
    void shouldDeliverAndAckMessage() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketSession alice = client.doHandshake(
                new TextWebSocketHandler() {},
                "ws://localhost:" + port + "/ws?token=VALID_JWT"
        ).get();

        alice.sendMessage(new TextMessage("""
            {
              "fromUser":"alice",
              "toUser":"bob",
              "payload":"hello"
            }
        """));

        // Assert via logs, kafka consumer spy, or registry mock
    }
}

