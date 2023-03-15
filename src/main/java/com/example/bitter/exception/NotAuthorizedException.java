package com.example.bitter.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NotAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = 7711165392054604223L;
	
	private String message;
    
}
