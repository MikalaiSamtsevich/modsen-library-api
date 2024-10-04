package com.libraryapp.libraryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "book_status")
@NoArgsConstructor
@AllArgsConstructor
public class BookStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "book_id", unique = true)
    private Long bookId;

    @Column(name = "borrowed_at")
    private LocalDateTime borrowedAt;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Builder.Default
    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;

}