package com.example.bitter.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDto {

    private CredentialsDto credentials;

    private ProfileDto profile;

}
