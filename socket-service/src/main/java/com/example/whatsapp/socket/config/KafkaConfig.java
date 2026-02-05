package com.example.whatsapp.socket.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.Receipt;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @SuppressWarnings("removal")
    @Bean
    public ProducerFactory<String, ChatMessage> chatProducerFactory(
            KafkaProperties kafkaProperties) {

        Map<String, Object> cfg =
                new HashMap<>(kafkaProperties.buildProducerProperties());

        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(cfg);
    }

    @Bean
    public KafkaTemplate<String, ChatMessage> chatKafkaTemplate(
            ProducerFactory<String, ChatMessage> chatProducerFactory) {
        return new KafkaTemplate<>(chatProducerFactory);
    }

    // OPTIONAL (only if socket-service sends receipts too)
    @SuppressWarnings("removal")
    @Bean
    public ProducerFactory<String, Receipt> receiptProducerFactory(
            KafkaProperties kafkaProperties) {

        Map<String, Object> cfg =
                new HashMap<>(kafkaProperties.buildProducerProperties());

        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(cfg);
    }

    @Bean
    public KafkaTemplate<String, Receipt> receiptKafkaTemplate(
            ProducerFactory<String, Receipt> receiptProducerFactory) {
        return new KafkaTemplate<>(receiptProducerFactory);
    }
}


