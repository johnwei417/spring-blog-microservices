package com.honglin.service;


import com.honglin.exceptions.DuplicateUserException;
import com.honglin.vo.UserDto;

public interface UserService {
    void save(UserDto user) throws DuplicateUserException;
}
