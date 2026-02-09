package com.example.whatsapp.socket.auth;

import com.example.whatsapp.socket.security.JwtHandshakeInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

class JwtHandshakeInterceptorTest {

    @Test
    void shouldRejectInvalidToken() {
        JwtHandshakeInterceptor i = new JwtHandshakeInterceptor("secret");
        Map<String, Object> attrs = new HashMap<>();

        boolean ok = i.beforeHandshake(
                null,
                new MockServerHttpRequest(HttpMethod.GET, URI.create("/ws")),
                null,
                attrs
        );

        assertThat(ok).isFalse();
    }
}

