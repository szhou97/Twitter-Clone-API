package com.example.bitter.service.impl;

import com.example.bitter.service.HashtagService;
import com.example.bitter.service.TweetService;

import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.bitter.repository.HashtagRepository;
import com.example.bitter.repository.TweetRepository;
import com.example.bitter.dto.HashtagDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.entity.Hashtag;
import com.example.bitter.entity.Tweet;
import com.example.bitter.exception.NotFoundException;
import com.example.bitter.mapper.HashtagMapper;
import com.example.bitter.mapper.TweetMapper;
@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService{
    
    private final HashtagRepository hashtagRepository;
    private final TweetRepository tweetRepository;
    private final HashtagMapper hashtagMapper;
    private final TweetMapper tweetMapper;
    
    
    @Override
    public List<HashtagDto> getAllTags() {
        return hashtagMapper.entitiesToDto(hashtagRepository.findAll());
    }

    @Override
    public HashtagDto getTagByLabel(String label) {
        Optional<Hashtag> tag = hashtagRepository.findByLabel(label);
        if (tag.isEmpty()) {
            throw new NotFoundException("Hashtag does not exist");
        }
        return hashtagMapper.entityToDto(tag.get());
    }
    
    @Override
    public List<TweetResponseDto> getAllTweetsWithTag(String label) {
        getTagByLabel(label); // check if tag exists
        return tweetMapper.entitiesToDtos(tweetRepository.findByDeletedFalseAndHashtags_LabelOrderByPosted(label));
    }
    @Override
    public HashtagDto updateTag(Tweet tweet, String label) {
        Optional<Hashtag> tag = hashtagRepository.findByLabel(label);
        Hashtag hashtagEntity;
        Date date = new Date();
        if (tag.isEmpty()) {

            //Create new Hashtag entry
            hashtagEntity = new Hashtag();
            hashtagEntity.setLabel(label);
            hashtagEntity.setFirstUsed(new Timestamp(date.getTime()));

        } else {

            // Update existing hashtag
            hashtagEntity = tag.get();
            
        }
        hashtagEntity.setLastUsed(new Timestamp(date.getTime()));
        hashtagEntity.getTweets().add(tweet);
        return hashtagMapper.entityToDto(hashtagRepository.saveAndFlush(hashtagEntity));
    }
}

