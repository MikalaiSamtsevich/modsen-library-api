package com.libraryapp.bookservice.event;

import com.libraryapp.bookservice.model.Book;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookHibernateEventListener implements PostDeleteEventListener, PostInsertEventListener {
    private final KafkaTemplate<String, Long> kafkaTemplate;

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof Book book) {
            kafkaTemplate.send("book_delete_topic", book.getId());
            log.info("Message {} sent to book topic book_delete_topic because of deleting book", book.getId());
        }
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof Book book) {
            kafkaTemplate.send("book_create_topic", book.getId());
            log.info("Message {} sent to book topic book_create_topic because of creating book", book.getId());
        }

    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persist) {
        return true;
    }

    @PostConstruct
    private void postConstruct() {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        assert registry != null;
        registry.prependListeners(EventType.POST_COMMIT_DELETE, this);
        registry.prependListeners(EventType.POST_COMMIT_INSERT, this);
    }
}