package com.example.bitter.mapper;

import com.example.bitter.dto.HashtagDto;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.bitter.entity.Hashtag;

@Mapper(componentModel = "spring")
public interface HashtagMapper {
    HashtagDto entityToDto (Hashtag hashtag);

    Hashtag dtoToEntity (HashtagDto hashtagDto);

    List<HashtagDto> entitiesToDto(List<Hashtag> hashtags);

    // TODO: Add more if necessary
}
