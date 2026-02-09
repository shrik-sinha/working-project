package com.example.whatsapp.socket.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    /* =====================
       Session lifecycle
       ===================== */

    public void register(String user, WebSocketSession session) {
        sessions.put(user, session);
    }

    public void remove(String user) {
        sessions.remove(user);
    }

    /* =====================
       ✅ NEW: Getter
       ===================== */

    public WebSocketSession getSessionByUser(String user) {
        return sessions.get(user);
    }

    public boolean isUserConnected(String user) {
        WebSocketSession session = sessions.get(user);
        return session != null && session.isOpen();
    }

    /* =====================
       Send helper
       ===================== */

    public void sendToUser(String user, Object payload) {
        WebSocketSession session = sessions.get(user);

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(
                        new TextMessage(mapper.writeValueAsString(payload))
                );
            } catch (Exception e) {
                // production: replace with structured logging
                log.error(String.valueOf(e));
            }
        }
    }
}
