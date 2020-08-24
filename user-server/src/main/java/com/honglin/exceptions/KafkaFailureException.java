package com.honglin.exceptions;

public class KafkaFailureException extends RuntimeException {
    public KafkaFailureException(String msg) {
        super(msg);
    }
}
