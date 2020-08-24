package com.honglin.httpclient;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class blogclientFallbackFactory implements FallbackFactory<blogClient> {
    @Override
    public blogClient create(Throwable throwable) {
        return new blogclientFallback(throwable);
    }
}
