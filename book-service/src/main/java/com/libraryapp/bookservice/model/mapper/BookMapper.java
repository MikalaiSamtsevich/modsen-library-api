package com.libraryapp.bookservice.model.mapper;

import com.libraryapp.bookservice.model.Book;
import com.libraryapp.bookservice.model.dto.RequestBookDtoV1;
import com.libraryapp.bookservice.model.dto.ResponseBookDtoV1;
import org.mapstruct.Mapping;

@org.mapstruct.Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = "spring", uses = {AuthorMapper.class})
public interface BookMapper {
    @Mapping(source = "authorId", target = "author.id")
    Book toEntity(RequestBookDtoV1 responseBookDtoV1);

    ResponseBookDtoV1 toDto(Book book);

    RequestBookDtoV1 toDtoWithoutId(Book book);

    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    Book partialUpdate(ResponseBookDtoV1 responseBookDtoV1, @org.mapstruct.MappingTarget Book book);

    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    Book partialUpdate(RequestBookDtoV1 responseBookDtoV1, @org.mapstruct.MappingTarget Book book);
}