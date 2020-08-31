package com.honglin.controller;


import com.honglin.entity.Roles;
import com.honglin.entity.User;
import com.honglin.exceptions.DuplicateUserException;
import com.honglin.service.RoleService;
import com.honglin.service.impl.UserServiceImpl;
import com.honglin.vo.ChangePasswordVO;
import com.honglin.vo.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class AuthController {

    private final UserServiceImpl userService;

    private final RoleService roleService;

    @Lazy
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserServiceImpl userService, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/createNewUser")
    public Integer createUser(@RequestBody UserDto user) throws DuplicateUserException {
        try {
            Optional<UserDto> userDto = Optional.of(user);
            try {
                userService.loadUserByUsername(userDto.get().getUsername());
                log.warn("Username already exist!");
                return HttpStatus.SC_BAD_REQUEST;
            } catch (UsernameNotFoundException e) {
                List<Roles> authorities = new ArrayList<>();
                authorities.add(roleService.getAuthorityById(1));
                userDto.get().setAuthorities(authorities);
                userService.save(userDto.get());
                log.info("User: " + userDto.get().getUsername() + " register success!");
                return HttpStatus.SC_OK;
            }
        } catch (NullPointerException ex) {
            log.warn("User is null, failed to create");
        }
        return HttpStatus.SC_OK;
    }

    @PostMapping("/deleteUser")
    public Integer deleteUser(@RequestBody UserDto user) {
        try {
            userService.delete(user);
        } catch (Exception e) {
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }
        log.info("User: " + user.getUsername() + " deleted success!");
        return HttpStatus.SC_OK;
    }

    @PostMapping("/changePassword")
    public Integer changePassword(@RequestBody ChangePasswordVO changePasswordVO, @RequestParam String username) {
        String oldPassword = changePasswordVO.getOldPassword();
        String newPassword = changePasswordVO.getNewPassword();

        User user = (User) userService.loadUserByUsername(username);
        String originalPassword = user.getPassword();

        log.info(username + " trying to change password");
        boolean isMatches = passwordEncoder.matches(oldPassword, originalPassword);
        if (isMatches) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.updatePassword(user);
            return HttpStatus.SC_OK;
        } else {
            return HttpStatus.SC_NOT_ACCEPTABLE;
        }
    }


}
