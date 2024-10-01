package com.libraryapp.bookservice.controller;

import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import com.libraryapp.bookservice.model.dto.UpdateBookDtoV1;
import com.libraryapp.bookservice.model.filter.BookFilter;
import com.libraryapp.bookservice.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final ValidationAutoConfiguration validationAutoConfiguration;

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

    @Operation(
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseBookDtoV1.class)
                            )
                    ),
            }
    )
    @PatchMapping("/{id}")
    public ResponseBookDtoV1 patch(
            @PathVariable Long id,
            @RequestBody UpdateBookDtoV1 updateBookDtoV1) {
        return bookService.patch(id, updateBookDtoV1);
    }

    @Operation(
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Books successfully updated. Returns a list of IDs of the updated books.",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Long.class)
                                    )
                            )
                    ),
            }
    )
    @PatchMapping
    public List<Long> patchMany(@RequestParam @Valid List<Long> ids, @RequestBody UpdateBookDtoV1 updateBookDtoV1) {
        return bookService.patchMany(ids, updateBookDtoV1);
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
