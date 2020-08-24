package com.honglin.httpclient;


import com.honglin.vo.blogUserDto;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class blogclientFallback implements blogClient {
    @Override
    public Integer sync(blogUserDto blogUserDto) {
        return HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }
}
