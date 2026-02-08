package com.example.whatsapp.common;

import java.util.UUID;

/**
 * Acknowledgement / receipt for a chat message.
 *
 * Used for:
 *  - DELIVERED (receiver device got the message)
 *  - READ (receiver opened the message)
 *
 * Emitted by:
 *  - socket-service
 *
 * Consumed by:
 *  - message-service
 *  - socket-service (to notify sender)
 */
public record Receipt(
        UUID messageId,
        String fromUser,      // who generated the receipt (receiver)
        String toUser,        // original sender
        MessageStatus status, // DELIVERED / READ
        long timestamp
) {}
