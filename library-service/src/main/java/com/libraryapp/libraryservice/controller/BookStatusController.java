package com.libraryapp.libraryservice.controller;

import com.libraryapp.libraryservice.model.dto.ResponseBookStatusDtoV1;
import com.libraryapp.libraryservice.model.dto.UpdateBookStatusDtoV1;
import com.libraryapp.libraryservice.service.BookStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/books/status")
@RequiredArgsConstructor
public class BookStatusController {

    private final BookStatusService bookStatusService;

    @GetMapping("/free")
    public Page<ResponseBookStatusDtoV1> getFreeBooks(@ParameterObject Pageable pageable) {
        return bookStatusService.getFreeBooks(pageable);
    }

    @GetMapping
    public Page<ResponseBookStatusDtoV1> getList(@ParameterObject Pageable pageable) {
        return bookStatusService.getList(pageable);
    }

    @GetMapping("/{id}")
    public ResponseBookStatusDtoV1 getOne(@PathVariable Long id) {
        return bookStatusService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<ResponseBookStatusDtoV1> getMany(@RequestParam List<Long> ids) {
        return bookStatusService.getMany(ids);
    }

    @PatchMapping("/{id}")
    @Operation(
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ResponseBookStatusDtoV1.class))
                    )
            }
    )
    public ResponseBookStatusDtoV1 patch(@PathVariable Long id, @RequestBody @Valid UpdateBookStatusDtoV1 updateBookStatusDtoV1) {
        return bookStatusService.patch(id, updateBookStatusDtoV1);
    }

    @PatchMapping
    @Operation(
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book statuses successfully updated. Returns a list of IDs of the updated book statuses.",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Long.class)
                                    )
                            )
                    )
            }
    )
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody @Valid UpdateBookStatusDtoV1 updateBookStatusDtoV1) {
        return bookStatusService.patchMany(ids, updateBookStatusDtoV1);
    }

}
