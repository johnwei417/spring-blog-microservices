package com.honglin.service.impl;

import com.honglin.dao.UserRepo;
import com.honglin.entity.User;
import com.honglin.exceptions.DuplicateUserException;
import com.honglin.service.UserService;
import com.honglin.vo.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    @Transactional(rollbackFor = DuplicateUserException.class, isolation = Isolation.REPEATABLE_READ)
    public void save(UserDto user) throws DuplicateUserException {
        if (userRepo.findByUsername(user.getUsername()) != null) {
            throw new DuplicateUserException("Duplicate username");
        }
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        userRepo.save(newUser);

        log.info(user.getUsername() + " added to db successfully");
    }

}
