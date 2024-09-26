package com.libraryapp.bookservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Pattern(message = "Invalid ISBN format", regexp = "^(97[89])?-?\\d{9}-?([0-9X])$")
    @Column(name = "isbn", nullable = false, unique = true, length = 13)
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