package com.example.whatsapp.socket.session;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionRegistry {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public WebSocketSession get(String userId) {
        return sessions.get(userId);
    }

    public void remove(WebSocketSession session) {
        sessions.values().remove(session);
    }
}
