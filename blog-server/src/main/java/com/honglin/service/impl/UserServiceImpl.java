package com.honglin.service.impl;

import com.honglin.dao.UserRepository;
import com.honglin.entity.User;
import com.honglin.exceptions.DuplicateUserException;
import com.honglin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User saveOrUpateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = DuplicateUserException.class, isolation = Isolation.REPEATABLE_READ)
    public User registerUser(User user) throws DuplicateUserException {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new DuplicateUserException("Duplicate username");
        }
        userRepository.save(user);
        log.info(user.getUsername() + " added to db successfully");
        return user;
    }

    @Transactional
    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Page<User> listUsersByNameLike(String name, Pageable pageable) {
        name = "%" + name + "%";
        Page<User> users = userRepository.findByUsernameLike(name, pageable);
        return users;
    }


    @Override
    public List<User> listUsersByUsernames(Collection<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }


}
