package com.example.bitter.dto;

import com.example.bitter.entity.Tweet;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class TweetResponseDto {
    private Integer id;

    private Timestamp posted;

    private boolean deleted;

    private UserResponseDto author;

    private String content;

    private TweetResponseDto inReplyTo;

    private TweetResponseDto repostOf;
}
