package com.libraryapp.libraryservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.libraryapp.libraryservice.model.dto.BookStatusDto;
import com.libraryapp.libraryservice.service.BookStatusService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/books/status")
@RequiredArgsConstructor
public class BookStatusController {

    private final BookStatusService bookStatusService;

    @GetMapping("/free")
    public Page<BookStatusDto> getFreeBooks(@ParameterObject Pageable pageable) {
        return bookStatusService.getFreeBooks(pageable);
    }

    @GetMapping
    public Page<BookStatusDto> getList(@ParameterObject Pageable pageable) {
        return bookStatusService.getList(pageable);
    }

    @GetMapping("/{id}")
    public BookStatusDto getOne(@PathVariable Long id) {
        return bookStatusService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<BookStatusDto> getMany(@RequestParam List<Long> ids) {
        return bookStatusService.getMany(ids);
    }

    @PatchMapping("/{id}")
    public BookStatusDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return bookStatusService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return bookStatusService.patchMany(ids, patchNode);
    }

}
