package com.ilhanozkan.libraryManagementSystem.model.mapper;

import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserResponseDTOMapper {
  UserResponseDTOMapper INSTANCE = Mappers.getMapper(UserResponseDTOMapper.class);

  UserResponseDTO toUserResponseDTO(User user);

  List<UserResponseDTO> toUserResponseDTOList(List<User> users);
}
