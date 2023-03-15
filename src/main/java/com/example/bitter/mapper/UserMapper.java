package com.example.bitter.mapper;

import com.example.bitter.dto.UserRequestDto;
import com.example.bitter.dto.UserResponseDto;
import com.example.bitter.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, CredentialsMapper.class}) // user tweets?
public interface UserMapper {

    @Mapping(target = "username", source = "credentials.username")
    UserResponseDto entityToDto(User entity);

    List<UserResponseDto> entitiesToDtos(List<User> entities);

    User dtoToEntity(UserRequestDto userRequestDto);

    User responseToEntity(UserResponseDto userResponseDto);

    List<User> responsesToEntities(List<UserResponseDto> userResponseDtos);

}
