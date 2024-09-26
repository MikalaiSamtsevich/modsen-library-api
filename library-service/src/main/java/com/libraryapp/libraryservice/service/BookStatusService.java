package com.libraryapp.libraryservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.libraryapp.libraryservice.model.dto.BookStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface BookStatusService {
    Page<BookStatusDto> getList(Pageable pageable);

    BookStatusDto getOne(Long id);

    List<BookStatusDto> getMany(List<Long> ids);


    BookStatusDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    Page<BookStatusDto> getFreeBooks(Pageable pageable);
}