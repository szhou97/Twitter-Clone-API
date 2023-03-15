package com.example.bitter.mapper;

import com.example.bitter.dto.TweetRequestDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.entity.Tweet;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface TweetMapper {
    TweetResponseDto entityToDto(Tweet entity);

    // TODO: List vs Set depends on endpoint requirements
    List<TweetResponseDto> entitiesToDtos(List<Tweet> entities);
    Tweet dtoToEntity(TweetRequestDto tweetRequestDto);

    Tweet responseToEntity(TweetResponseDto tweetResponseDto);
}
