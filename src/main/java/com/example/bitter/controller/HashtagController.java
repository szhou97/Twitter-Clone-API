package com.example.bitter.controller;

import com.example.bitter.dto.HashtagDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.service.HashtagService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class HashtagController {
    private final HashtagService hashtagService;

    @GetMapping
    public List<HashtagDto> getAllTags() {
        return hashtagService.getAllTags();
    }

    @GetMapping("/{label}")
    public List<TweetResponseDto> getAllTweetsWithTag(@PathVariable String label) {
        return hashtagService.getAllTweetsWithTag(label);
    }
}