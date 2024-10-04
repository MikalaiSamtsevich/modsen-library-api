package com.libraryapp.libraryservice.service.impl;

import com.libraryapp.libraryservice.model.BookStatus;
import com.libraryapp.libraryservice.model.dto.ResponseBookStatusDtoV1;
import com.libraryapp.libraryservice.model.dto.UpdateBookStatusDtoV1;
import com.libraryapp.libraryservice.model.mapper.BookStatusMapper;
import com.libraryapp.libraryservice.repository.BookStatusRepository;
import com.libraryapp.libraryservice.util.DataUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@Tag("unit")
@ExtendWith(MockitoExtension.class)
class BookStatusServiceImplTest {

    @InjectMocks
    private BookStatusServiceImpl bookStatusService;

    @Mock
    private BookStatusRepository bookStatusRepository;

    @Mock
    private BookStatusMapper bookStatusMapper;

    @Test
    void shouldReturnPagedListOfFreeBookStatusDto() {
        // given
        Pageable pageable = Pageable.unpaged();
        BookStatus bookStatus = DataUtils.createBookStatusEntityPersisted();
        ResponseBookStatusDtoV1 responseDto = DataUtils.createResponseBookStatusDto();

        when(bookStatusRepository.findAllFreeBooks(pageable)).thenReturn(new PageImpl<>(List.of(bookStatus)));
        when(bookStatusMapper.toDto(bookStatus)).thenReturn(responseDto);

        // when
        Page<ResponseBookStatusDtoV1> result = bookStatusService.getFreeBooks(pageable);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().getFirst());

        verify(bookStatusRepository).findAllFreeBooks(pageable);
        verify(bookStatusMapper).toDto(bookStatus);
    }

    @Test
    void shouldReturnPagedListOfBookStatusDto() {
        // given
        Pageable pageable = Pageable.unpaged();
        BookStatus bookStatus = DataUtils.createBookStatusEntityPersisted();
        ResponseBookStatusDtoV1 responseDto = DataUtils.createResponseBookStatusDto();

        when(bookStatusRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(bookStatus)));
        when(bookStatusMapper.toDto(bookStatus)).thenReturn(responseDto);

        // when
        Page<ResponseBookStatusDtoV1> result = bookStatusService.getList(pageable);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().getFirst());

        verify(bookStatusRepository).findAll(pageable);
        verify(bookStatusMapper).toDto(bookStatus);
    }

    @Test
    void shouldReturnBookStatusDtoWhenBookStatusExists() {
        // given
        Long id = 1L;
        BookStatus bookStatus = DataUtils.createBookStatusEntityPersisted();
        ResponseBookStatusDtoV1 responseDto = DataUtils.createResponseBookStatusDto();

        when(bookStatusRepository.findById(id)).thenReturn(Optional.of(bookStatus));
        when(bookStatusMapper.toDto(bookStatus)).thenReturn(responseDto);

        // when
        ResponseBookStatusDtoV1 result = bookStatusService.getOne(id);

        // then
        assertEquals(responseDto, result);

        verify(bookStatusRepository).findById(id);
        verify(bookStatusMapper).toDto(bookStatus);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookStatusDoesNotExist() {
        // given
        Long id = 999L;

        when(bookStatusRepository.findById(id)).thenReturn(Optional.empty());

        // when
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> bookStatusService.getOne(id));

        // then
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("Entity with id `999` not found", thrown.getReason());

        verify(bookStatusRepository).findById(id);
        verifyNoMoreInteractions(bookStatusRepository);
        verifyNoInteractions(bookStatusMapper);
    }

    @Test
    void shouldReturnBookStatusesWhenIdsAreProvided() {
        // given
        List<Long> ids = Arrays.asList(1L, 2L);
        BookStatus bookStatus1 = DataUtils.createBookStatusEntityPersisted();
        BookStatus bookStatus2 = DataUtils.createBookStatusEntityPersisted();

        ResponseBookStatusDtoV1 responseDto1 = DataUtils.createResponseBookStatusDto();
        ResponseBookStatusDtoV1 responseDto2 = DataUtils.createResponseBookStatusDto();

        when(bookStatusRepository.findAllById(ids)).thenReturn(Arrays.asList(bookStatus1, bookStatus2));
        when(bookStatusMapper.toDto(bookStatus1)).thenReturn(responseDto1);
        when(bookStatusMapper.toDto(bookStatus2)).thenReturn(responseDto2);

        // when
        List<ResponseBookStatusDtoV1> result = bookStatusService.getMany(ids);

        // then
        assertEquals(2, result.size());
        assertEquals(responseDto1, result.get(0));
        assertEquals(responseDto2, result.get(1));

        verify(bookStatusRepository).findAllById(ids);
        verify(bookStatusMapper).toDto(bookStatus1);
        verify(bookStatusMapper).toDto(bookStatus2);
    }

    @Test
    void shouldUpdateBookStatusWhenExists() {
        // given
        Long id = 1L;
        UpdateBookStatusDtoV1 updateBookStatusDto = DataUtils.createUpdateBookStatusDto();
        BookStatus existingBookStatus = DataUtils.createBookStatusEntityPersisted();
        existingBookStatus.setId(id);
        existingBookStatus.setIsFree(false);

        ResponseBookStatusDtoV1 expectedResponseDto = DataUtils.createResponseBookStatusDto();

        when(bookStatusRepository.findById(id)).thenReturn(Optional.of(existingBookStatus));
        when(bookStatusRepository.save(existingBookStatus)).thenReturn(existingBookStatus);
        when(bookStatusMapper.toDto(any(BookStatus.class))).thenReturn(expectedResponseDto);

        // when
        ResponseBookStatusDtoV1 result = bookStatusService.patch(id, updateBookStatusDto);

        // then
        assertThat(result).isEqualTo(expectedResponseDto);
        assertThat(existingBookStatus.getBorrowedAt()).isEqualTo(updateBookStatusDto.getBorrowedAt());
        assertThat(existingBookStatus.getDueDate()).isEqualTo(updateBookStatusDto.getDueDate());
        assertThat(existingBookStatus.getIsFree()).isEqualTo(updateBookStatusDto.getIsFree());

        verify(bookStatusRepository).findById(id);
        verify(bookStatusRepository).save(existingBookStatus);
        verify(bookStatusMapper).toDto(existingBookStatus);
    }

    @Test
    void shouldThrowExceptionWhenBookStatusDoesNotExist() {
        // given
        Long id = 1L;
        UpdateBookStatusDtoV1 updateDto = DataUtils.createUpdateBookStatusDto();

        when(bookStatusRepository.findById(id)).thenReturn(Optional.empty());

        // when
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                bookStatusService.patch(id, updateDto));

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Book Status with id `1` not found", exception.getReason());

        verify(bookStatusRepository).findById(id);
        verifyNoMoreInteractions(bookStatusRepository);
        verifyNoInteractions(bookStatusMapper);
    }

    @Test
    void shouldPatchManyBookStatuses() {
        // given
        List<Long> ids = List.of(1L, 2L);
        UpdateBookStatusDtoV1 updateBookStatusDto = DataUtils.createUpdateBookStatusDto();

        BookStatus bookStatus1 = DataUtils.createBookStatusEntityPersisted();
        bookStatus1.setId(1L);
        BookStatus bookStatus2 = DataUtils.createBookStatusEntityPersisted();
        bookStatus2.setId(2L);

        List<BookStatus> bookStatuses = List.of(bookStatus1, bookStatus2);

        when(bookStatusRepository.findAllById(ids)).thenReturn(bookStatuses);
        when(bookStatusRepository.saveAll(bookStatuses)).thenReturn(bookStatuses);

        // when
        List<Long> resultIds = bookStatusService.patchMany(ids, updateBookStatusDto);

        // then
        assertEquals(ids, resultIds);
        assertEquals(updateBookStatusDto.getBorrowedAt(), bookStatus1.getBorrowedAt());
        assertEquals(updateBookStatusDto.getDueDate(), bookStatus1.getDueDate());
        assertEquals(updateBookStatusDto.getIsFree(), bookStatus1.getIsFree());

        assertEquals(updateBookStatusDto.getBorrowedAt(), bookStatus2.getBorrowedAt());
        assertEquals(updateBookStatusDto.getDueDate(), bookStatus2.getDueDate());
        assertEquals(updateBookStatusDto.getIsFree(), bookStatus2.getIsFree());

        verify(bookStatusRepository).findAllById(ids);
        verify(bookStatusRepository).saveAll(bookStatuses);
    }

}