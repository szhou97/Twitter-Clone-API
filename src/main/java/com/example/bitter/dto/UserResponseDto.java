package com.example.bitter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class UserResponseDto {

    private String username;

    private ProfileDto profile;

    private Timestamp joined;

}
