package com.libraryapp.bookservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Bean
    org.springframework.kafka.core.DefaultKafkaProducerFactory<String, String> stringProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties(null);
        producerProperties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        producerProperties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.LongSerializer.class);
        return new org.springframework.kafka.core.DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    org.springframework.kafka.core.KafkaTemplate<String, String> stringKafkaTemplate(org.springframework.kafka.core.DefaultKafkaProducerFactory<String, String> stringProducerFactory) {
        return new org.springframework.kafka.core.KafkaTemplate<>(stringProducerFactory);
    }

    @Bean
    public org.springframework.kafka.core.ConsumerFactory<String, String> stringConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.LongDeserializer.class);
        return new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public org.springframework.kafka.config.KafkaListenerContainerFactory<?> stringListenerFactory(org.springframework.kafka.core.ConsumerFactory<String, String> stringConsumerFactory) {
        org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<String, String> factory = new org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory);
        factory.setBatchListener(false);
        return factory;
    }

    @Bean
    DefaultKafkaProducerFactory<String, Long> longProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties(null);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    KafkaTemplate<String, Long> longKafkaTemplate(DefaultKafkaProducerFactory<String, Long> longProducerFactory) {
        return new KafkaTemplate<>(longProducerFactory);
    }
}