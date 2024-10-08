package com.libraryapp.bookservice.service;

import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import com.libraryapp.bookservice.model.dto.UpdateBookDtoV1;
import com.libraryapp.bookservice.model.filter.BookFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Page<ResponseBookDtoV1> getList(BookFilter filter, Pageable pageable);

    ResponseBookDtoV1 getOne(Long id);

    List<ResponseBookDtoV1> getMany(List<Long> ids);

    ResponseBookDtoV1 create(RequestBookDtoV1 dto);

    ResponseBookDtoV1 patch(Long id, UpdateBookDtoV1 updateBookDtoV1);

    List<Long> patchMany(List<Long> ids, UpdateBookDtoV1 updateBookDto);

    ResponseBookDtoV1 delete(Long id);

    void deleteMany(List<Long> ids);
}
