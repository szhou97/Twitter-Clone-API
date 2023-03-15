package com.example.bitter.service;

import com.example.bitter.dto.CredentialsDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.dto.UserRequestDto;
import com.example.bitter.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto getUserByUsername(String username);

    UserResponseDto deleteUserByUsername(String username);

    UserResponseDto updateUserProfileByUsername(String username, UserRequestDto userRequestDto);

    List<UserResponseDto> getUsersFollowedByUsername(String username);

    List<UserResponseDto> getFollowersOfTheUser (String username);

    List<TweetResponseDto> getTweets(String username);

    void follow(String username, CredentialsDto credentials);

    void unfollow(String username, CredentialsDto credentials);

    List<TweetResponseDto> getUserFeedByUsername(String username);

    List<TweetResponseDto> getMentionsOfUser(String username);
}
