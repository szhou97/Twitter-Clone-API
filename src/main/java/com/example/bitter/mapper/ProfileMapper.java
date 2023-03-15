package com.example.bitter.mapper;

import com.example.bitter.dto.ProfileDto;
import com.example.bitter.entity.Profile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileDto entityToDto(Profile profile);

    List<ProfileDto> entitiesToDto(List<Profile> profiles);

}
