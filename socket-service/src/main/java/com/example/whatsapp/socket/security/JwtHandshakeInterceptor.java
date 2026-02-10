package com.example.whatsapp.socket.security;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final String secret = "local-secret"; // override via profile later

    @Override
    public boolean beforeHandshake(    ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getQuery();

        if (query != null) {
            // Splitting by & handles multiple params safely
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1 && "user".equals(pair[0])) {
                    attributes.put("user", pair[1]);
                    return true;
                }
            }
        }
        return false; // Blocks the connection if no user is found
    }

    @Override
    public void afterHandshake(
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response,
            org.springframework.web.socket.WebSocketHandler wsHandler,
            Exception exception
    ) {}
}
