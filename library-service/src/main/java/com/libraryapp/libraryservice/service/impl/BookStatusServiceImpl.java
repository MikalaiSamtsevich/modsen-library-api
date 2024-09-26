package com.libraryapp.libraryservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.libraryservice.model.BookStatus;
import com.libraryapp.libraryservice.model.dto.BookStatusDto;
import com.libraryapp.libraryservice.model.dto.BookStatusWithoutIdDto;
import com.libraryapp.libraryservice.model.mapper.BookStatusMapper;
import com.libraryapp.libraryservice.repository.BookStatusRepository;
import com.libraryapp.libraryservice.service.BookStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookStatusServiceImpl implements BookStatusService {

    private final BookStatusMapper bookStatusMapper;

    private final BookStatusRepository bookStatusRepository;

    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<BookStatusDto> getList(Pageable pageable) {
        Page<BookStatus> bookStatuses = bookStatusRepository.findAll(pageable);
        return bookStatuses.map(bookStatusMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public BookStatusDto getOne(Long id) {
        Optional<BookStatus> bookStatusOptional = bookStatusRepository.findById(id);
        return bookStatusMapper.toDto(bookStatusOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookStatusDto> getMany(List<Long> ids) {
        List<BookStatus> bookStatuses = bookStatusRepository.findAllById(ids);
        return bookStatuses.stream()
                .map(bookStatusMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public BookStatusDto patch(Long id, JsonNode patchNode) throws IOException {
        BookStatus bookStatus = bookStatusRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        BookStatusWithoutIdDto bookStatusDto = bookStatusMapper.toDtoWithoutId(bookStatus);
        objectMapper.readerForUpdating(bookStatusDto).readValue(patchNode);
        bookStatusMapper.updateWithNull(bookStatusDto, bookStatus);

        BookStatus resultBookStatus = bookStatusRepository.save(bookStatus);
        return bookStatusMapper.toDto(resultBookStatus);
    }

    @Transactional
    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<BookStatus> bookStatuses = bookStatusRepository.findAllById(ids);

        for (BookStatus bookStatus : bookStatuses) {
            BookStatusWithoutIdDto bookStatusDto = bookStatusMapper.toDtoWithoutId(bookStatus);
            objectMapper.readerForUpdating(bookStatusDto).readValue(patchNode);
            bookStatusMapper.updateWithNull(bookStatusDto, bookStatus);
        }

        List<BookStatus> resultBookStatuses = bookStatusRepository.saveAll(bookStatuses);
        return resultBookStatuses.stream()
                .map(BookStatus::getId)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookStatusDto> getFreeBooks(Pageable pageable) {
        Page<BookStatus> bookStatuses = bookStatusRepository.findAllFreeBooks(pageable);
        return bookStatuses.map(bookStatusMapper::toDto);
    }

}
