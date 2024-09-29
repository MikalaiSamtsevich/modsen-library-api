package com.libraryapp.bookservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import com.libraryapp.bookservice.model.filter.BookFilter;
import com.libraryapp.bookservice.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    @GetMapping
    public Page<ResponseBookDtoV1> getList(@ParameterObject @ModelAttribute BookFilter filter, @ParameterObject Pageable pageable) {
        return bookService.getList(filter, pageable);
    }

    @GetMapping("/{id}")
    public ResponseBookDtoV1 getOne(@PathVariable Long id) {
        return bookService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<ResponseBookDtoV1> getMany(@RequestParam List<Long> ids) {
        return bookService.getMany(ids);
    }

    @PostMapping
    public ResponseBookDtoV1 create(@RequestBody @Valid RequestBookDtoV1 dto) {
        return bookService.create(dto);
    }

    @PatchMapping("/{id}")
    public ResponseBookDtoV1 patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return bookService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam @Valid List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return bookService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public ResponseBookDtoV1 delete(@PathVariable Long id) {
        return bookService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        bookService.deleteMany(ids);
    }
}
