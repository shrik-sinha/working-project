package com.example.whatsapp.message.config;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.Receipt;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    /* =========================
       CHAT MESSAGE CONSUMER
       ========================= */

    @SuppressWarnings("removal")
    @Bean
    public ConsumerFactory<String, ChatMessage> chatConsumerFactory(
            KafkaProperties kafkaProperties) {

        Map<String, Object> cfg =
                new HashMap<>(kafkaProperties.buildConsumerProperties());

        // 🔥 CRITICAL: remove auto-configured deserializer
        cfg.remove(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
        cfg.remove(JsonDeserializer.TRUSTED_PACKAGES);

        cfg.put(ConsumerConfig.GROUP_ID_CONFIG, "message-service-chat");

        JsonDeserializer<ChatMessage> deserializer =
                new JsonDeserializer<>(ChatMessage.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                cfg,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessage>
    chatKafkaListenerContainerFactory(
            ConsumerFactory<String, ChatMessage> chatConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, ChatMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(chatConsumerFactory);
        return factory;
    }

    /* =========================
       RECEIPT CONSUMER
       ========================= */

    @SuppressWarnings("removal")
    @Bean
    public ConsumerFactory<String, Receipt> receiptConsumerFactory(
            KafkaProperties kafkaProperties) {

        Map<String, Object> cfg =
                new HashMap<>(kafkaProperties.buildConsumerProperties());

        cfg.remove(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
        cfg.remove(JsonDeserializer.TRUSTED_PACKAGES);

        cfg.put(ConsumerConfig.GROUP_ID_CONFIG, "message-service-receipts");

        JsonDeserializer<Receipt> deserializer =
                new JsonDeserializer<>(Receipt.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                cfg,
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Receipt>
    receiptKafkaListenerContainerFactory(
            ConsumerFactory<String, Receipt> receiptConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, Receipt> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(receiptConsumerFactory);
        return factory;
    }
}
