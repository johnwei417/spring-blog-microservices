package com.honglin.common;

import com.honglin.exceptions.DuplicateUserException;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(value = DuplicateUserException.class)
//    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public CommonResponse handleDuplicateUserException(DuplicateUserException ex) {
        return new CommonResponse(HttpStatus.SC_BAD_REQUEST, ex.getMessage());
    }
}
