package com.libraryapp.libraryservice.model.mapper;

import com.libraryapp.libraryservice.model.BookStatus;
import com.libraryapp.libraryservice.model.dto.ResponseBookStatusDtoV1;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookStatusMapper {
    ResponseBookStatusDtoV1 toDto(BookStatus bookStatus);
}