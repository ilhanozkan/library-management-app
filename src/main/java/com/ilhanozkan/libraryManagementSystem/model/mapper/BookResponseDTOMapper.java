package com.ilhanozkan.libraryManagementSystem.model.mapper;

import com.ilhanozkan.libraryManagementSystem.model.dto.response.BookResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookResponseDTOMapper {
  BookResponseDTOMapper INSTANCE = Mappers.getMapper(BookResponseDTOMapper.class);

  BookResponseDTO toBookResponseDTO(Book book);

  List<BookResponseDTO> toBookResponseDTOList(List<Book> books);
}
