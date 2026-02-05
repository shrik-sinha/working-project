package com.example.whatsapp.common;

public record Envelope(
        String type,   // "CHAT" or "RECEIPT"
        String data    // JSON string of ChatMessage or Receipt
) {}
