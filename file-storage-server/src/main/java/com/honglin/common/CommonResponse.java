package com.honglin.common;

import lombok.Data;

@Data
public class CommonResponse<T> {
    int code;
    String message;
    T data;

    public CommonResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public CommonResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
