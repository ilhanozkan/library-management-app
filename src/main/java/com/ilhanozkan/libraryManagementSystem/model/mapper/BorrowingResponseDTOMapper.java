package com.ilhanozkan.libraryManagementSystem.model.mapper;

import com.ilhanozkan.libraryManagementSystem.model.dto.response.BorrowingResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.Borrowing;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BorrowingResponseDTOMapper {
  BorrowingResponseDTOMapper INSTANCE = Mappers.getMapper(BorrowingResponseDTOMapper.class);

  BorrowingResponseDTO toBorrowingResponseDTO(Borrowing borrowing);

  List<BorrowingResponseDTO> toBorrowingResponseDTOList(List<Borrowing> borrowings);
}
