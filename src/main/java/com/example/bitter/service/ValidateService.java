package com.example.bitter.service;

public interface ValidateService {
    public Boolean tagExists(String label);
    public Boolean usernameExists(String username);
    public Boolean usernameAvailable(String username);
}
