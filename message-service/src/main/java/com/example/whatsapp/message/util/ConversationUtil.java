package com.example.whatsapp.message.util;

public final class ConversationUtil {

    private ConversationUtil() {
        // utility class
    }

    public static String conversationId(String u1, String u2) {
        return u1.compareTo(u2) < 0
                ? u1 + "#" + u2
                : u2 + "#" + u1;
    }
}
