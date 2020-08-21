package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.service.UserService;
import com.honglin.vo.UserDto;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public CommonResponse addUser(@RequestBody UserDto userDto) {
        try {
            userService.save(userDto);
        } catch (Exception e) {
            return new CommonResponse(HttpStatus.SC_BAD_REQUEST, userDto.getUsername() + " already exist!");
        }
        return new CommonResponse(HttpStatus.SC_OK, userDto.getUsername() + " add to blog success");
    }
}
