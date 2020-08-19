package com.honglin.common;

import com.honglin.exceptions.DuplicateUserException;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserContollerAdvice {

    @ExceptionHandler(value = DuplicateUserException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public CommonResponse duplicateUser(DuplicateUserException ex) {
        return new CommonResponse(HttpStatus.SC_BAD_REQUEST, ex.getMessage());
    }
}
