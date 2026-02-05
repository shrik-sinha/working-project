package com.example.whatsapp.common;

import java.util.UUID;

public record ChatMessage(
        UUID messageId,
        String fromUser,
        String toUser,
        String payload,
        long timestamp
) {}
