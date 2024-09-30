package com.libraryapp.bookservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaBookProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    public void sendBookCreatedMessage(Long bookId) {
        kafkaTemplate.send("book_create_topic", bookId);
        log.info("Message {} sent to topic book_create_topic", bookId);
    }

    public void sendBookDeletedMessage(Long bookId) {
        kafkaTemplate.send("book_delete_topic", bookId);
        log.info("Message {} sent to topic book_delete_topic", bookId);
    }
}