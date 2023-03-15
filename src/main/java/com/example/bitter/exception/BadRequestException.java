package com.example.bitter.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 7815295729166407620L;
    private String message;
}
