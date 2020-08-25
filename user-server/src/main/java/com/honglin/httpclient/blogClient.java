package com.honglin.httpclient;


import com.honglin.vo.blogUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "blog-service", fallback = blogclientFallback.class)
public interface blogClient {
    /**
     * call blog server to create
     *
     * @param blogUserDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/users/addUser", consumes = "application/json")
    Integer sync(blogUserDto blogUserDto);

}
