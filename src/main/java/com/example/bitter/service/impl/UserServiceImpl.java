package com.example.bitter.service.impl;

import com.example.bitter.dto.CredentialsDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.dto.UserRequestDto;
import com.example.bitter.dto.UserResponseDto;
import com.example.bitter.entity.Tweet;
import com.example.bitter.entity.User;
import com.example.bitter.exception.BadRequestException;
import com.example.bitter.exception.NotAuthorizedException;
import com.example.bitter.exception.NotFoundException;
import com.example.bitter.mapper.TweetMapper;
import com.example.bitter.mapper.UserMapper;
import com.example.bitter.repository.TweetRepository;
import com.example.bitter.repository.UserRepository;
import com.example.bitter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final TweetMapper tweetMapper;

    private final TweetRepository tweetRepository;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse());
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User userToSave = userMapper.dtoToEntity(userRequestDto);
        if (userRequestDto.getCredentials() == null) {
            throw new BadRequestException("Credentials can't be null.");
        }
        if (userRequestDto.getCredentials().getUsername() == null) {
            throw new BadRequestException("The provided username is null.");
        }
        if (userRequestDto.getCredentials().getPassword() == null) {
            throw new BadRequestException("The provided password is null.");
        }
        if (userRequestDto.getProfile() == null) {
            throw new BadRequestException("Profile can't be null.");
        }
        if (userRequestDto.getProfile().getEmail() == null) {
            throw new BadRequestException("The provided email is null.");
        }
        if (userRepository.existsByCredentials_Username(userRequestDto.getCredentials().getUsername())) {
            User userByUsername = userRepository.findUserByCredentials_Username(userRequestDto.getCredentials().getUsername());
            if (userByUsername.isDeleted()) {
                userByUsername.setDeleted(false);
                if (userToSave.getProfile().getFirstName() != null) {
                    userByUsername.getProfile().setFirstName((userToSave.getProfile().getFirstName()));
                }
                if (userToSave.getProfile().getLastName() != null) {
                    userByUsername.getProfile().setLastName(userToSave.getProfile().getLastName());
                }
                if (userToSave.getProfile().getPhone() != null) {
                    userByUsername.getProfile().setPhone(userRequestDto.getProfile().getPhone());
                }
                if (userToSave.getProfile().getEmail() != null) {
                    userByUsername.getProfile().setEmail(userRequestDto.getProfile().getEmail());
                }
                userToSave = userByUsername;
            } else {
                throw new BadRequestException("user already exists.");
            }
        }

        userRepository.saveAndFlush(userToSave);

        return userMapper.entityToDto(userToSave);
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        if (!userRepository.existsByCredentials_Username(username)) {
            throw new NotFoundException("The provided username doesn't exist.");
        }
        if (userRepository.findUserByCredentials_Username(username).isDeleted()) {
            throw new BadRequestException("The provided username was deleted.");
        }
        User userByCredentialsUsername = userRepository.findUserByCredentials_Username(username);

        return userMapper.entityToDto(userByCredentialsUsername);
    }

    @Override
    public UserResponseDto deleteUserByUsername(String username) {
        if (!userRepository.existsByCredentials_Username(username)) {
            throw new NotFoundException("The provided username doesn't exist.");
        }
        if (userRepository.findUserByCredentials_Username(username).isDeleted()) {
            throw new BadRequestException("The provided username is deleted already.");
        }
        User userToDelete = userRepository.findUserByCredentials_Username(username);
        userToDelete.setDeleted(true);

        userRepository.saveAndFlush(userToDelete);

        return userMapper.entityToDto(userToDelete);
    }

    @Override
    public UserResponseDto updateUserProfileByUsername(String username, UserRequestDto userRequestDto) {
        User updatedUser = userMapper.dtoToEntity(userRequestDto);
        if (updatedUser.getCredentials() == null) {
            throw new BadRequestException("Credentials can't be null.");
        }
        if (!userRepository.existsByCredentials_Username(username)) {
            throw new NotFoundException("The provided username doesn't exist.");
        }
        if (!username.equals(updatedUser.getCredentials().getUsername())) {
            throw new NotAuthorizedException("The provided username doesn't match provided credentials.");
        }
        if (!userRepository.existsByCredentials(updatedUser.getCredentials())) {
            throw new NotAuthorizedException("The provided credentials don't match our records.");
        }
        if (userRepository.findUserByCredentials_Username(username).isDeleted()) {
            throw new BadRequestException("The provided username is deleted.");
        }
        if (updatedUser.getProfile() == null) {
            throw new BadRequestException("Profile can't be null.");
        }
        User userToUpdate = userRepository.findUserByCredentials_Username(username);
        if (updatedUser.getProfile().getFirstName() == null) {
            updatedUser.getProfile().setFirstName(userToUpdate.getProfile().getFirstName());
        }
        if (updatedUser.getProfile().getLastName() == null) {
            updatedUser.getProfile().setLastName(userToUpdate.getProfile().getLastName());
        }
        if (updatedUser.getProfile().getEmail() == null) {
            updatedUser.getProfile().setEmail(userToUpdate.getProfile().getEmail());
        }
        if (updatedUser.getProfile().getPhone() == null) {
            updatedUser.getProfile().setPhone(userToUpdate.getProfile().getPhone());
        }
        userToUpdate.setProfile(updatedUser.getProfile());
        userRepository.saveAndFlush(userToUpdate);

        return userMapper.entityToDto(userToUpdate);
    }

    @Override
    public List<TweetResponseDto> getTweets(String username) {
        User user;
        try {
            user = userRepository.findUserByCredentials_Username(username);
        } catch (NotFoundException e) {
            throw e;
        }
        if (user == null) throw new NotFoundException("User " + username + " doesn't exist.");
        List<Tweet> tweets = user.getTweets();
        tweets.removeIf(Tweet::isDeleted);
        return tweetMapper.entitiesToDtos(tweets);
    }

    @Override
    public void follow(String username, CredentialsDto credentials) {
        verifyUsers(username, credentials);
        User sourceUser = userRepository.findUserByCredentials_Username(credentials.getUsername());
        User targetUser = userRepository.findUserByCredentials_Username(username);

        if (targetUser.getFollowers().contains(sourceUser) || sourceUser.getFollowing().contains(targetUser)) {
            throw new BadRequestException("Already following");
        }

        sourceUser.getFollowing().add(targetUser);
        targetUser.getFollowers().add(sourceUser);

        userRepository.saveAndFlush(sourceUser);
        userRepository.saveAndFlush(targetUser);
    }

    @Override
    public void unfollow(String username, CredentialsDto credentials) {
        verifyUsers(username, credentials);
        User sourceUser = userRepository.findUserByCredentials_Username(credentials.getUsername());
        User targetUser = userRepository.findUserByCredentials_Username(username);
        if (!targetUser.getFollowers().contains(sourceUser) || !sourceUser.getFollowing().contains(targetUser)) {
            throw new BadRequestException("Not following");
        }

        sourceUser.getFollowing().remove(targetUser);
        targetUser.getFollowers().remove(sourceUser);

        userRepository.saveAndFlush(sourceUser);
        userRepository.saveAndFlush(targetUser);
    }

    @Override
    public List<TweetResponseDto> getUserFeedByUsername(String username) {
        if (!userRepository.existsByCredentials_Username(username) || userRepository.findUserByCredentials_Username(username).isDeleted()) {
            throw new NotFoundException("The provided username doesn't exist.");
        }
        List<UserResponseDto> usersFollowedByUsername = getUsersFollowedByUsername(username);
        List<String> usernames = new ArrayList<>();
        usersFollowedByUsername.forEach(userResponseDto -> {
            String usernameOfAuthor = userResponseDto.getUsername();
            usernames.add(usernameOfAuthor);
        });

        List <User> feedAuthors = userRepository.findUsersByCredentials_UsernameIn(usernames);
        User user = userRepository.findUserByCredentials_Username(username);
        feedAuthors.add(user);
        List<Tweet> feed = tweetRepository.findAllByDeletedFalseAndAuthorInOrderByPosted(feedAuthors);

        return tweetMapper.entitiesToDtos(feed);
    }

    @Override
    public List<TweetResponseDto> getMentionsOfUser(String username) {
        if (!userRepository.existsByCredentials_Username(username) || userRepository.findUserByCredentials_Username(username).isDeleted()) {
            throw new NotFoundException("The provided username doesn't exist.");
        }
        User userByUserName = userRepository.findUserByCredentials_Username(username);
        List<Tweet> mentions = userByUserName.getMentions();

        return tweetMapper.entitiesToDtos(mentions);
    }

    private void verifyUsers(String username, CredentialsDto credentials) {
        if (credentials == null || credentials.getUsername() == null || credentials.getPassword() == null) {
            throw new BadRequestException("Credentials can't be null.");
        }
        if (!userRepository.existsByCredentials_Username(credentials.getUsername())) {
            throw new NotFoundException("The provided source username doesn't exist.");
        }
        if (!userRepository.existsByCredentials_Username(username)) {
            throw new NotFoundException("The provided target username doesn't exist.");
        }
    }

    @Override
    public List<UserResponseDto> getFollowersOfTheUser(String username) {
        if (!userRepository.existsByCredentials_Username(username) || userRepository.findUserByCredentials_Username(username).isDeleted()) {
            throw new NotFoundException("The provided username doesn't exist.");
        }
        User userByUsername = userRepository.findUserByCredentials_Username(username);
        List<User> followers = userByUsername.getFollowers();

        return userMapper.entitiesToDtos(followers);
    }

    @Override
    public List<UserResponseDto> getUsersFollowedByUsername(String username) {
        if (!userRepository.existsByCredentials_Username(username) || userRepository.findUserByCredentials_Username(username).isDeleted()) {
            throw new NotFoundException("The provided username doesn't exist.");
        }
        User userByUsername = userRepository.findUserByCredentials_Username(username);
        List<User> usersFollowedByUsername = userByUsername.getFollowing();

        return userMapper.entitiesToDtos(usersFollowedByUsername);
    }

}
