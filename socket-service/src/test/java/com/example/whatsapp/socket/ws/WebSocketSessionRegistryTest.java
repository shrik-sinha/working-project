package com.example.whatsapp.socket.ws;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.Mockito.mock;

class WebSocketSessionRegistryTest {

    @Test
    void shouldRegisterAndFetchSession() {
        WebSocketSessionRegistry r = new WebSocketSessionRegistry();
        WebSocketSession session = mock(WebSocketSession.class);

        r.register("alice", session);

        assertThat(r.getSessionByUser("alice")).isEqualTo(session);
    }
}

