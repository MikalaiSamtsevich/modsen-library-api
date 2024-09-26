package com.libraryapp.bookservice.model.mapper;

import com.libraryapp.bookservice.model.Author;

@org.mapstruct.Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(com.libraryapp.bookservice.model.dto.AuthorDto authorDto);

    com.libraryapp.bookservice.model.dto.AuthorDto toDto(Author author);

    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    Author partialUpdate(com.libraryapp.bookservice.model.dto.AuthorDto authorDto, @org.mapstruct.MappingTarget Author author);
}