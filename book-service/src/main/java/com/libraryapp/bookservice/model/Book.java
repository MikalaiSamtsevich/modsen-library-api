package com.libraryapp.bookservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "book")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Pattern(regexp = "^(97[89])?\\d{9}[0-9X]$", message = "Invalid ISBN format")
    @Column(name = "isbn", nullable = false, unique = true, length = 25)
    private String isbn;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "title", nullable = false)
    private String title;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.MERGE,
            CascadeType.REFRESH
    }, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genre genre;

}