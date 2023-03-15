package com.example.bitter.service.impl;

import com.example.bitter.entity.Hashtag;
import com.example.bitter.exception.BadRequestException;
import com.example.bitter.exception.NotFoundException;
import com.example.bitter.service.HashtagService;
import com.example.bitter.service.UserService;
import com.example.bitter.service.ValidateService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final HashtagService hashtagService;
    private final UserService userService;
    @Override
    public Boolean tagExists(String label) {
        try {
            hashtagService.getTagByLabel(label);
        } catch (NotFoundException nfe) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean usernameExists(String username) {
        try {
            userService.getUserByUsername(username);
        } catch (NotFoundException nfe) {
            return false;
        } catch (BadRequestException bre) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean usernameAvailable(String username) {
        return !usernameExists(username);
    }
    
}
