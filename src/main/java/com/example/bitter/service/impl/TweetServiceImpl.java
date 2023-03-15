package com.example.bitter.service.impl;

import com.example.bitter.dto.ContextDto;
import com.example.bitter.dto.CredentialsDto;
import com.example.bitter.dto.HashtagDto;
import com.example.bitter.dto.TweetRequestDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.dto.UserResponseDto;
import com.example.bitter.entity.Hashtag;
import com.example.bitter.entity.Tweet;
import com.example.bitter.entity.User;
import com.example.bitter.exception.BadRequestException;
import com.example.bitter.exception.NotFoundException;
import com.example.bitter.mapper.CredentialsMapper;
import com.example.bitter.mapper.HashtagMapper;

import com.example.bitter.mapper.TweetMapper;
import com.example.bitter.mapper.UserMapper;
import com.example.bitter.repository.HashtagRepository;
import com.example.bitter.repository.TweetRepository;
import com.example.bitter.repository.UserRepository;
import com.example.bitter.service.TweetService;

import ch.qos.logback.core.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final HashtagMapper hashtagMapper;
    private final HashtagRepository hashtagRepository;

    public Tweet getTweetIfExists(Long id) {
        Optional<Tweet> tweet = tweetRepository.findById(id);
        if (tweet.isEmpty() || tweet.get().isDeleted()) throw new NotFoundException("Tweet " + id + " not found");
        return tweet.get();
    }

    public void validateCredentials(CredentialsDto credentialsDto) {
        if (credentialsDto == null
                || credentialsDto.getUsername() == null
                || credentialsDto.getPassword() == null) throw new BadRequestException("Incomplete credentials");
    }

    // Must be in reverse-chronological order
    @Override
    public List<TweetResponseDto> getAllTweets() {
        return tweetMapper.entitiesToDtos(tweetRepository.findByDeletedFalseOrderByPosted());
    }

    // Throw error if no such tweet exists or is deleted
    @Override
    public TweetResponseDto getTweet(Long id) {
        return tweetMapper.entityToDto(getTweetIfExists(id));
    }

    public Tweet parseAndAddMentions(Tweet tweet) {
        Pattern pattern = Pattern.compile("(@+[a-zA-Z0-9(_)]{1,})");
        Set<User> mentions = new HashSet<>();
        Matcher matcher = pattern.matcher(tweet.getContent());

        // parse usernames, then update tweet mentions for each user
        while (matcher.find()) {
            String username = matcher.group(0).substring(1);
            User user = userRepository.findUserByCredentials_Username(username);
            List<Tweet> t = user.getMentions();
            Tweet savedTweet = tweetRepository.saveAndFlush(tweet); // literally won't work without doing this
            t.add(savedTweet);
            user.setMentions(t);
            userRepository.save(user);
            mentions.add(userRepository.saveAndFlush(user));
        }

        // update tweet
        tweet.setMentioned(mentions);
        return tweetRepository.saveAndFlush(tweet);
    }

    public Tweet parseAndAddHashtags(Tweet tweet) {
        Pattern pattern = Pattern.compile("(#+[a-zA-Z0-9(_)]{1,})");
        Set<Hashtag> tags = new HashSet<>();
        Matcher matcher = pattern.matcher(tweet.getContent());

        // parse hashtags
        while (matcher.find()) {
            String label = matcher.group(0).substring(1);
            Optional<Hashtag> optionalTag = hashtagRepository.findByLabel(label);
            Hashtag tag;
            // update existing tags or create a new tag, then add the tag
            if (optionalTag.isPresent()) {
                tag = optionalTag.get();
            } else {
                tag = new Hashtag();
                tag.setLabel(label);
                tag.setFirstUsed(Timestamp.valueOf(LocalDateTime.now()));
                tag.setTweets(new ArrayList<>());
            }
            tag.setLastUsed(Timestamp.valueOf(LocalDateTime.now()));
            List<Tweet> t = tag.getTweets();
            t.add(tweet);
            tag.setTweets(t);
            tags.add(tag);
        }
        hashtagRepository.saveAllAndFlush(tags);

        tweet.setHashtags(tags);
        return tweetRepository.saveAndFlush(tweet);
    }

    // Create simple tweet, w/ author set to the user identified by the credentials in the request body
    // Must contain content property and proper credentials, otherwise throw error
    // Must parse @usernames and #hashtags
    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
        return tweetMapper.entityToDto(createTweetEntity(tweetRequestDto));
    }

    @Override
    public TweetResponseDto replyTweet(Long id, TweetRequestDto tweetRequestDto) {
        Tweet targetTweet = getTweetIfExists(id);
        Tweet sourceTweet = createTweetEntity(tweetRequestDto);
        targetTweet.getReplies().add(sourceTweet);
        sourceTweet.setInReplyTo(targetTweet);
        tweetRepository.saveAndFlush(sourceTweet);
        tweetRepository.saveAndFlush(targetTweet);
        
        return tweetMapper.entityToDto(sourceTweet);
    }

    private Tweet createTweetEntity(TweetRequestDto tweetRequestDto) {
        if (tweetRequestDto.getCredentials() == null) throw new BadRequestException("No credentials provided");
        validateCredentials(tweetRequestDto.getCredentials());
        // check if user exists
        User user;
        user = userRepository.findUserByCredentials_Username(tweetRequestDto.getCredentials().getUsername());
        if (user == null) throw new BadRequestException("Invalid credentials");

        if (tweetRequestDto.getContent() == null) throw new BadRequestException("New tweet must contain content");
        Tweet tweet = tweetMapper.dtoToEntity(tweetRequestDto);

        tweet.setAuthor(userRepository.findUserByCredentials_Username(tweetRequestDto.getCredentials().getUsername()));

        Tweet updatedTweetWithMentions = parseAndAddMentions(tweet);
        Tweet updatedTweetWithHashtags = parseAndAddHashtags(updatedTweetWithMentions);

        return updatedTweetWithHashtags;
    }

    // Throw error is the tweet is deleted or doesn't exist, or if the credentials don't match an active user in the DB
    // On successful operation, return no response body
    @Override
    public void likeTweet(Long id, CredentialsDto credentialsDto) {
        validateCredentials(credentialsDto);
        Tweet tweet = getTweetIfExists(id);
        User user;
        try {
            user = userRepository.findUserByCredentials_Username(credentialsDto.getUsername());
        } catch (NotFoundException e) {
            throw e;
        }
        if (user == null) throw new BadRequestException("Invalid credentials");

        Set<User> u = tweet.getLikedBy();
        u.add(user);
        tweet.setLikedBy(u);
        Tweet savedTweet = tweetRepository.saveAndFlush(tweet);

        List<Tweet> t = user.getLikes();
        t.add(savedTweet);
        userRepository.saveAndFlush(user);
    }


    @Override
    public List<UserResponseDto> getUsersWhoLikedTweet(Long id) {
        Tweet tweet = getTweetIfExists(id);
        List<User> users = new ArrayList<>(tweet.getLikedBy());
        users.removeIf(User::isDeleted);
        return userMapper.entitiesToDtos(users);
    }

    @Override
    public List<UserResponseDto> getTweetMentions(Long id) {
        Tweet tweet = getTweetIfExists(id);
        List<User> users = new ArrayList<>(tweet.getMentioned());
        users.removeIf(User::isDeleted);
        return userMapper.entitiesToDtos(users);
    }

    @Override
    public List<TweetResponseDto> getRepliesToTweet(Long id) {
        Tweet tweet = getTweetIfExists(id);
        List<Tweet> replies = new ArrayList<>(tweet.getReplies());
        replies.removeIf(Tweet::isDeleted);
        return tweetMapper.entitiesToDtos(replies);
    }

    // Throw error is the tweet is deleted or doesn't exist, or if the credentials don't match an active user in the DB
    // No content, author of the repost should match the credentials provided in the request body
    @Override
    public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {
        validateCredentials(credentialsDto);
        Tweet tweet = getTweetIfExists(id);
        Tweet newTweet = new Tweet();
        newTweet.setAuthor(userRepository.findUserByCredentials_Username(credentialsDto.getUsername()));
        newTweet.setRepostOf(tweet);

        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(newTweet));
    }

    @Override
    public List<TweetResponseDto> getRepostsOfTweet(Long id) {
        Tweet tweet = getTweetIfExists(id);
        List<Tweet> reposts = new ArrayList<>(tweet.getReposts());
        reposts.removeIf(Tweet::isDeleted);
        return tweetMapper.entitiesToDtos(reposts);
    }

    @Override
    public TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto) {
        Tweet tweet = getTweetIfExists(id);
        tweet.setDeleted(true);
        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweet));
    }

    @Override
    public List<HashtagDto> getTagsByTweetId(Long id) {
        return hashtagMapper.entitiesToDto(hashtagRepository.findByTweets_Id(id));
    }

    @Override
    public ContextDto getContextByTweetId(Long id) {
        Tweet tweet = getTweetIfExists(id);
        Tweet previousTweet = tweet.getInReplyTo();
        List<TweetResponseDto> before = new ArrayList<>();
        while (previousTweet != null) {
            if (!previousTweet.isDeleted()) {
                before.add(tweetMapper.entityToDto(previousTweet));
            }
            previousTweet = previousTweet.getInReplyTo();
        }

        List<TweetResponseDto> after = new ArrayList<>();
        for (Tweet t : tweet.getReplies()) {
            if (!t.isDeleted()) {
                after.add(tweetMapper.entityToDto(t));
            }
        }

        ContextDto contextDto = new ContextDto();
        contextDto.setTarget(tweetMapper.entityToDto(tweet));
        contextDto.setBefore(before);
        contextDto.setAfter(after);
        return contextDto;
    }

    
}
