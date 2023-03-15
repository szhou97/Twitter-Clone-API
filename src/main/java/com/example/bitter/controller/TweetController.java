package com.example.bitter.controller;

import com.example.bitter.dto.ContextDto;
import com.example.bitter.dto.CredentialsDto;
import com.example.bitter.dto.HashtagDto;
import com.example.bitter.dto.TweetRequestDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.dto.UserResponseDto;
import com.example.bitter.entity.Tweet;
import com.example.bitter.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {
    private final TweetService tweetService;

    // POST tweets
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto createTweet(@RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.createTweet(tweetRequestDto);
    }

    // GET tweets
    @GetMapping
    public List<TweetResponseDto> getAllTweets() {
        return tweetService.getAllTweets();
    }

    // GET tweets/{id}
    @GetMapping("/{id}")
    public TweetResponseDto getTweet(@PathVariable Long id) {
        return tweetService.getTweet(id);
    }

    // GET tweets/{id}/tags
    @GetMapping("/{id}/tags")
    public List<HashtagDto> getTagsByTweetId(@PathVariable Long id) {
        return tweetService.getTagsByTweetId(id);
    }


    // POST tweets/{id}/like
    @PostMapping("/{id}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void likeTweet(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
        tweetService.likeTweet(id, credentialsDto);
    }

    //GET tweets/{id}/likes
    @GetMapping("/{id}/likes")
    public List<UserResponseDto> getUsersWhoLikedTweet(@PathVariable Long id) {
        return tweetService.getUsersWhoLikedTweet(id);
    }

    // GET tweets/{id}/mentions
    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getTweetMentions(@PathVariable Long id) {
        return tweetService.getTweetMentions(id);
    }

    // POST tweets/{id}/reply
    // TODO: Shawn
    @PostMapping("/{id}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto replyTweet(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.replyTweet(id, tweetRequestDto);
    }
    // GET tweets/{id}/replies
    @GetMapping("/{id}/replies")
    public List<TweetResponseDto> getRepliesToTweet(@PathVariable Long id) {
        return tweetService.getRepliesToTweet(id);
    }

    // POST tweets/{id}/repost
    @PostMapping("/{id}/repost")
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto repostTweet(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
        return tweetService.repostTweet(id, credentialsDto);
    }

    // GET tweets/{id}/reposts
    @GetMapping("/{id}/reposts")
    public List<TweetResponseDto> getRepostsOfTweet(@PathVariable Long id) {
        return tweetService.getRepostsOfTweet(id);
    }

    // GET tweets/{id}/context
    // TODO: Shawn
    @GetMapping("/{id}/context")
    public ContextDto getContextByTweet(@PathVariable Long id) {
        return tweetService.getContextByTweetId(id);
    }


    // DELETE tweets/{id}
    @DeleteMapping("/{id}")
    public TweetResponseDto deleteTweet(@PathVariable Long id, CredentialsDto credentialsDto) {
        return tweetService.deleteTweet(id, credentialsDto);
    }
}
