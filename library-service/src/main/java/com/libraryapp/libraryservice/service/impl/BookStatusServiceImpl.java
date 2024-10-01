package com.libraryapp.libraryservice.service.impl;

import com.libraryapp.libraryservice.model.BookStatus;
import com.libraryapp.libraryservice.model.dto.ResponseBookStatusDtoV1;
import com.libraryapp.libraryservice.model.dto.UpdateBookStatusDtoV1;
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookStatusServiceImpl implements BookStatusService {

    private final BookStatusMapper bookStatusMapper;

    private final BookStatusRepository bookStatusRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ResponseBookStatusDtoV1> getFreeBooks(Pageable pageable) {
        Page<BookStatus> bookStatuses = bookStatusRepository.findAllFreeBooks(pageable);
        return bookStatuses.map(bookStatusMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ResponseBookStatusDtoV1> getList(Pageable pageable) {
        Page<BookStatus> bookStatuses = bookStatusRepository.findAll(pageable);
        return bookStatuses.map(bookStatusMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseBookStatusDtoV1 getOne(Long id) {
        Optional<BookStatus> bookStatusOptional = bookStatusRepository.findById(id);
        return bookStatusMapper.toDto(bookStatusOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseBookStatusDtoV1> getMany(List<Long> ids) {
        List<BookStatus> bookStatuses = bookStatusRepository.findAllById(ids);
        return bookStatuses.stream()
                .map(bookStatusMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public ResponseBookStatusDtoV1 patch(Long id, UpdateBookStatusDtoV1 updateBookStatusDto) {
        BookStatus bookStatus = bookStatusRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Book Status with id `%s` not found".formatted(id)));

        updateBookStatusFields(bookStatus, updateBookStatusDto);

        BookStatus resultBookStatus = bookStatusRepository.save(bookStatus);
        return bookStatusMapper.toDto(resultBookStatus);
    }

    @Transactional
    @Override
    public List<Long> patchMany(List<Long> ids, UpdateBookStatusDtoV1 updateBookStatusDto) {
        Collection<BookStatus> bookStatuses = bookStatusRepository.findAllById(ids);

        for (BookStatus bookStatus : bookStatuses) {
            updateBookStatusFields(bookStatus, updateBookStatusDto);
        }

        List<BookStatus> resultBookStatuses = bookStatusRepository.saveAll(bookStatuses);
        return resultBookStatuses.stream()
                .map(BookStatus::getId)
                .toList();
    }

    private void updateBookStatusFields(BookStatus bookStatus, UpdateBookStatusDtoV1 updateBookStatusDto) {
        if (updateBookStatusDto.getBorrowedAt() != null) {
            bookStatus.setBorrowedAt(updateBookStatusDto.getBorrowedAt());
        }
        if (updateBookStatusDto.getDueDate() != null) {
            bookStatus.setDueDate(updateBookStatusDto.getDueDate());
        }
        if (updateBookStatusDto.getIsFree() != null) {
            bookStatus.setIsFree(updateBookStatusDto.getIsFree());
        }
    }

}
