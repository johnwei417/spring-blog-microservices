package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.User;
import com.honglin.exceptions.DuplicateUserException;
import com.honglin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(org.springframework.http.HttpStatus.OK)
    @PostMapping("/addUser")
    public Integer addUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
        } catch (DuplicateUserException e) {
            log.error(user.getUsername() + " already exist!");
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }

        return HttpStatus.SC_OK;
    }

    @GetMapping("/searchAllUsers")
    public CommonResponse<List<User>> list(@RequestParam(value = "async", required = false) boolean async,
                                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                           @RequestParam(value = "username", required = false, defaultValue = "") String username) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<User> page = userService.listUsersByNameLike(username, pageable);
        List<User> list = page.getContent();    // get user data at current page
        return new CommonResponse<>(HttpStatus.SC_OK, "Search users success!", list);
    }

}
