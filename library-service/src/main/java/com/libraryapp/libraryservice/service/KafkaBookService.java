package com.libraryapp.libraryservice.service;

import com.libraryapp.libraryservice.model.BookStatus;
import com.libraryapp.libraryservice.repository.BookStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaBookService {
    private final BookStatusRepository bookStatusRepository;
    @Value("${app.weeks-until-due}")
    private Integer weeksUntilDue;

    @Transactional
    @KafkaListener(topics = "book_create_topic", containerFactory = "longListenerFactory")
    public void consumeAndCreate(Long id) {
        log.info("Received id {} in book_create_topic", id);
        BookStatus bookStatus = new BookStatus();
        bookStatus.setBookId(id);
        bookStatus.setIsFree(false);
        bookStatus.setBorrowedAt(LocalDateTime.now());
        bookStatus.setDueDate(LocalDateTime.now().plusWeeks(weeksUntilDue));
        bookStatusRepository.save(bookStatus);
    }

    @Transactional
    @KafkaListener(topics = "book_delete_topic", containerFactory = "longListenerFactory")
    public void consumeAndDelete(Long id) {
        log.info("Received id {} in book_delete_topic", id);
        bookStatusRepository.deleteBookStatusByBookId(id);
    }
}
