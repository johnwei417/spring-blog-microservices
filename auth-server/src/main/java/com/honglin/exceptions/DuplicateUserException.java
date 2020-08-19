package com.honglin.exceptions;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String msg) {
        super(msg);
    }
}
