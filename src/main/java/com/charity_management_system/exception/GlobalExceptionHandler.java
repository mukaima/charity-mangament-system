package com.charity_management_system.exception;

import com.charity_management_system.exception.custom.CaseNotFoundException;
import com.charity_management_system.exception.custom.CategoryNotFoundException;
import com.charity_management_system.exception.custom.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles BadCredentialsException and returns a response with HTTP status 401.
     *
     * @param ex The BadCredentialsException.
     * @return A response entity with the exception message and HTTP status 401.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler({CaseNotFoundException.class, UserNotFoundException.class, CategoryNotFoundException.class})
    public ResponseEntity<String> handleNotFoundExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
