package com.honglin.service;

import com.honglin.entity.User;
import com.honglin.vo.UserDto;

public interface UserService {
    void save(UserDto user);

    void delete(UserDto user);

    void updatePassword(User user);
}
