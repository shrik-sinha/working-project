package com.example.whatsapp.socket.session;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionRegistry {

    private final ConcurrentHashMap<String, WebSocketSession> sessions =
            new ConcurrentHashMap<>();

    public void add(String user, WebSocketSession session) {
        sessions.put(user, session);
    }

    public WebSocketSession get(String user) {
        return sessions.get(user);
    }

    public void remove(String user) {
        sessions.remove(user);
    }
}
