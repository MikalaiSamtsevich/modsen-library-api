package com.libraryapp.bookservice.model.filter;

import com.libraryapp.bookservice.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public record BookFilter(String isbnLike) {
    public Specification<Book> toSpecification() {
        return Specification.where(isbnLikeSpec());
    }

    private Specification<Book> isbnLikeSpec() {
        return ((root, query, cb) -> StringUtils.hasText(isbnLike)
                ? cb.like(cb.lower(root.get("isbn")), "%" + isbnLike.toLowerCase() + "%")
                : null);
    }
}