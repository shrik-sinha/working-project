package com.example.whatsapp.common;

import java.util.UUID;

public record Receipt(
        UUID messageId,
        String fromUser,
        String toUser,
        MessageStatus status,
        long timestamp
) {}
