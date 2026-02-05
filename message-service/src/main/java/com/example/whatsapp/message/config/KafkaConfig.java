package com.example.whatsapp.message.config;

import com.example.whatsapp.common.ChatMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
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

    @Bean
    public ConsumerFactory<String, ChatMessage> chatConsumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "message-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(ChatMessage.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessage>
    chatKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ChatMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(chatConsumerFactory());
        return factory;
    }
}




/*@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, ChatMessage> chatProducerFactory() {
        Map<String, Object> props = new HashMap<>();

        // 🔑 REQUIRED
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // 🔑 KEY + VALUE serializers
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // ✅ IMPORTANT: avoid type headers mismatch across services
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ChatMessage> chatKafkaTemplate(
            ProducerFactory<String, ChatMessage> chatProducerFactory) {

        return new KafkaTemplate<>(chatProducerFactory);
    }
}*/


/*@Configuration
public class KafkaConfig {

    *//* =========================
       CHAT MESSAGE CONSUMER
       ========================= *//*

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

    *//* =========================
       RECEIPT CONSUMER
       ========================= *//*

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
} */
