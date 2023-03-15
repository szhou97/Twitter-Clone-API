package com.example.bitter.mapper;

import com.example.bitter.dto.CredentialsDto;
import com.example.bitter.dto.ProfileDto;
import com.example.bitter.entity.Credentials;

import com.example.bitter.entity.Profile;
import org.mapstruct.Mapper;

import javax.swing.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {

    CredentialsDto entityToDto(Credentials credentials);

    List<CredentialsDto> entitiesToDto(List<Credentials> credentials);

}
