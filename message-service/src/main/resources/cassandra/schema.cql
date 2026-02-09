CREATE KEYSPACE IF NOT EXISTS whatsapp
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };