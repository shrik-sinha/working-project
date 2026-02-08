package com.example.whatsapp.socket.config;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.Receipt;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return props;
    }

    // 🔹 ChatMessage producer
    @Bean
    public ProducerFactory<String, ChatMessage> chatProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProps());
    }

    @Bean
    public KafkaTemplate<String, ChatMessage> chatKafkaTemplate() {
        return new KafkaTemplate<>(chatProducerFactory());
    }

    // 🔹 Receipt producer
    @Bean
    public ProducerFactory<String, Receipt> receiptProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProps());
    }

    @Bean
    public KafkaTemplate<String, Receipt> receiptKafkaTemplate() {
        return new KafkaTemplate<>(receiptProducerFactory());
    }
}
