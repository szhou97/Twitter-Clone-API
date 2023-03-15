package com.example.bitter.dto;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HashtagDto {


    private Timestamp firstUsed;

    private String label;

    private Timestamp lastUsed;

}
