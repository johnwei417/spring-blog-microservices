package com.honglin.httpclient;


import com.honglin.common.CommonResponse;
import com.honglin.vo.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("blog-service")
public interface blogClient {
    /**
     * call blog server to create
     *
     * @param userDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/syncUser", consumes = "application/json")
    CommonResponse sync(UserDto userDto);

}
