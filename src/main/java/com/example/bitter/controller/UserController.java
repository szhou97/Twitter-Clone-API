package com.example.bitter.controller;

import com.example.bitter.dto.CredentialsDto;
import com.example.bitter.dto.TweetResponseDto;
import com.example.bitter.dto.UserRequestDto;
import com.example.bitter.dto.UserResponseDto;
import com.example.bitter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @GetMapping("/@{username}")
    public UserResponseDto getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @DeleteMapping("/@{username}")
    public UserResponseDto deleteUserByUsername(@PathVariable String username) {
        return userService.deleteUserByUsername(username);
    }

    // TODO: doesn't pass the tests for some reason
    @PatchMapping("/@{username}")
    public UserResponseDto updateUserProfileByUsername(@PathVariable String username, @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUserProfileByUsername(username, userRequestDto);
    }

    @GetMapping("/@{username}/following")
    public List<UserResponseDto> getUsersFollowedByUsername(@PathVariable String username){
        return userService.getUsersFollowedByUsername(username);
    }
    @GetMapping("/@{username}/followers")
    public List<UserResponseDto> getFollowersOfTheUser(@PathVariable String username){
        return userService.getFollowersOfTheUser(username);
    }

    @GetMapping("/@{username}/tweets")
    public List<TweetResponseDto> getTweets(@PathVariable String username){
        return userService.getTweets(username);
    }

    @PostMapping("/@{username}/follow")
    public void followUser(@PathVariable String username, @RequestBody CredentialsDto credentials) {
        userService.follow(username, credentials);
    }

    @PostMapping("/@{username}/unfollow")
    public void unfollowUser(@PathVariable String username, @RequestBody CredentialsDto credentials) {
        userService.unfollow(username, credentials);
    }

    @GetMapping("/@{username}/feed")
    public  List<TweetResponseDto> getUserFeedByUsername(@PathVariable String username){
        return userService.getUserFeedByUsername(username);
    }

    @GetMapping("/@{username}/mentions")
    public List<TweetResponseDto> getMentionsOfUser(@PathVariable String username){
        return userService.getMentionsOfUser(username);
    }

}
