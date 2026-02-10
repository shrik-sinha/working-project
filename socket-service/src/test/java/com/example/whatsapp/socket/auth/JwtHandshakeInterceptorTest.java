package com.example.whatsapp.socket.auth;

import com.example.whatsapp.socket.security.JwtHandshakeInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtHandshakeInterceptorTest {

    @Test
    void shouldRejectRequestWithoutAuthorizationHeader() throws Exception {
        HandshakeInterceptor interceptor = new JwtHandshakeInterceptor();

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/ws");

        ServerHttpRequest request =
                new ServletServerHttpRequest(servletRequest);

        Map<String, Object> attributes = new HashMap<>();

        boolean result = interceptor.beforeHandshake(
                request,
                null,
                null,
                attributes
        );

        assertThat(result).isFalse();
    }
}
