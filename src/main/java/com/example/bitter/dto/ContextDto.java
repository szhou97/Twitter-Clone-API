package com.example.bitter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ContextDto {
    private TweetResponseDto target;

    // must be in chronological order
    private List<TweetResponseDto> before;
    private List<TweetResponseDto> after;
}
