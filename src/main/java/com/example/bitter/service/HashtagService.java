package com.example.bitter.service;

import java.util.List;

import com.example.bitter.dto.HashtagDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.entity.Tweet;

public interface HashtagService {

    HashtagDto getTagByLabel(String label);

    List<HashtagDto> getAllTags();

    List<TweetResponseDto> getAllTweetsWithTag(String label);

    HashtagDto updateTag(Tweet tweet, String label);
}
