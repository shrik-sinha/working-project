package com.example.whatsapp.message.service;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.message.entity.ChatMessageEntity;
import com.example.whatsapp.message.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final ChatMessageRepository repo;

    public MessageService(ChatMessageRepository repo) {
        this.repo = repo;
    }

    public void save(ChatMessageEntity message) {
        repo.save(message);
    }
}

