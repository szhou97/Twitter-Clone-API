package com.example.bitter.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2214496748407564737L;
    
	private String message;
}
