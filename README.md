# Global WhatsApp Clone (Prototype)

Real-time messaging application inspired by WhatsApp, built with:
- **Backend**: Spring Boot (multi-module)
- **Database**: Apache Cassandra (for high-write scalability)
- **Real-time**: WebSocket + STOMP (socket-service)
- **Deployment**: Docker Compose (Cassandra + services)

## Modules
- **common**      : Shared models, DTOs, utilities
- **message-service** : Message storage, retrieval, conversation logic (Cassandra entities/repositories)
- **socket-service**  : WebSocket server for live delivery, read receipts, typing, online status

## Features Implemented / Planned
- [x] One-to-one messaging with delivery/read receipts
- [ ] Group chats
- [ ] Media upload (images/videos)
- [ ] End-to-end encryption simulation
- [ ] Offline message queuing
- [ ] User presence (online/offline)

## Quick Start (Local)
1. Start dependencies:
   ```bash
   docker-compose up -d cassandra