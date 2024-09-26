package com.libraryapp.libraryservice.repository;

import com.libraryapp.libraryservice.model.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookStatusRepository extends JpaRepository<BookStatus, Long>, JpaSpecificationExecutor<BookStatus> {

    @Query("SELECT bs FROM BookStatus bs WHERE bs.isFree = true")
    Page<BookStatus> findAllFreeBooks(Pageable pageable);
}