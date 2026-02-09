package com.example.whatsapp.socket.config;

import com.example.whatsapp.common.ChatMessage;
import com.example.whatsapp.common.Receipt;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public ConsumerFactory<String, ChatMessage> chatConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "socket-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<ChatMessage> deserializer =
                new JsonDeserializer<>(ChatMessage.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "chatKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessage>
    chatKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ChatMessage>();
        factory.setConsumerFactory(chatConsumerFactory());
        return factory;
    }

    // 🔹 RECEIPT CONSUMER
    @Bean
    public ConsumerFactory<String, Receipt> receiptConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "socket-receipts");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<Receipt> deserializer =
                new JsonDeserializer<>(Receipt.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean(name = "receiptKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Receipt>
    receiptKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Receipt> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(receiptConsumerFactory());
        return factory;
    }


}




