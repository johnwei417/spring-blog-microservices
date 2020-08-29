package com.honglin.controller;


import com.honglin.entity.Roles;
import com.honglin.exceptions.DuplicateUserException;
import com.honglin.service.RoleService;
import com.honglin.service.impl.UserServiceImpl;
import com.honglin.vo.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class AuthController {

    private final UserServiceImpl userService;

    private final RoleService roleService;

    @Autowired
    public AuthController(UserServiceImpl userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/createNewUser")
    public void createUser(@RequestBody UserDto user) {
        try {
            Optional<UserDto> userDto = Optional.of(user);
            try {
                userService.loadUserByUsername(userDto.get().getUsername());
                log.warn("Username already exist!");
                throw new DuplicateUserException("Username already exist!");
            } catch (UsernameNotFoundException e) {
                List<Roles> authorities = new ArrayList<>();
                authorities.add(roleService.getAuthorityById(1));
                userDto.get().setAuthorities(authorities);
                userService.save(userDto.get());
                log.info("User: " + userDto.get().getUsername() + " register success!");
            }
        } catch (NullPointerException ex) {
            log.warn("User is null, failed to create");
        }
    }

    @PostMapping("/deleteUser")
    public void deleteUser(@RequestBody UserDto user) {
        userService.delete(user);
        log.info("User: " + user.getUsername() + " deleted success!");
    }


}
