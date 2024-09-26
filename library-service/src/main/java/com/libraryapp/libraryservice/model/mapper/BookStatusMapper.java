package com.libraryapp.libraryservice.model.mapper;

import com.libraryapp.libraryservice.model.BookStatus;
import com.libraryapp.libraryservice.model.dto.BookStatusDto;
import com.libraryapp.libraryservice.model.dto.BookStatusWithoutIdDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookStatusMapper {
    BookStatus toEntity(BookStatusDto bookStatusDto);

    BookStatusDto toDto(BookStatus bookStatus);

    BookStatusWithoutIdDto toDtoWithoutId(BookStatus bookStatus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BookStatus partialUpdate(BookStatusDto bookStatusDto, @MappingTarget BookStatus bookStatus);

    BookStatus updateWithNull(BookStatusDto bookStatusDto, @MappingTarget BookStatus bookStatus);

    BookStatus updateWithNull(BookStatusWithoutIdDto bookStatusDto, @MappingTarget BookStatus bookStatus);
}