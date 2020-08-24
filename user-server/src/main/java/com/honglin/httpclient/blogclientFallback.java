package com.honglin.httpclient;


import com.honglin.common.CommonResponse;
import com.honglin.vo.blogUserDto;
import feign.FeignException;
import org.apache.http.HttpStatus;


public class blogclientFallback implements blogClient {
    private final Throwable cause;

    public blogclientFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public CommonResponse sync(blogUserDto blogUserDto) {
        if (cause instanceof FeignException && ((FeignException) cause).status() == 404) {
            // Treat the HTTP 404 status
            return new CommonResponse(HttpStatus.SC_NOT_FOUND, "feign client error");
        }

        return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "blog service not available now");
    }
}
