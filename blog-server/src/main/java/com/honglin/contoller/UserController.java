package com.honglin.contoller;

import com.honglin.exceptions.DuplicateUserException;
import com.honglin.service.UserService;
import com.honglin.vo.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public Integer addUser(@RequestBody UserDto userDto) {
        try {
            userService.save(userDto);
        } catch (DuplicateUserException e) {
            log.error(userDto.getUsername() + " already exist!");
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }

        return HttpStatus.SC_OK;
    }
}
