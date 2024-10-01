package com.libraryapp.libraryservice.service;

import com.libraryapp.libraryservice.model.dto.ResponseBookStatusDtoV1;
import com.libraryapp.libraryservice.model.dto.UpdateBookStatusDtoV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookStatusService {
    Page<ResponseBookStatusDtoV1> getList(Pageable pageable);

    ResponseBookStatusDtoV1 getOne(Long id);

    List<ResponseBookStatusDtoV1> getMany(List<Long> ids);


    ResponseBookStatusDtoV1 patch(Long id, UpdateBookStatusDtoV1 updateBookStatusDtoV1);

    List<Long> patchMany(List<Long> ids, UpdateBookStatusDtoV1 updateBookStatusDtoV1);

    Page<ResponseBookStatusDtoV1> getFreeBooks(Pageable pageable);
}