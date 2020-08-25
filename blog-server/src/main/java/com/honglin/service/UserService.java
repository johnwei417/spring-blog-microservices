package com.honglin.service;


import com.honglin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;


public interface UserService {
    /**
     * find user by username
     *
     * @param username
     * @return
     */
    User findUserByUsername(String username);

    /**
     * add, edit, save user
     *
     * @param user
     * @return
     */
    User saveOrUpateUser(User user);

    /**
     * register new user
     *
     * @param user
     * @return
     */
    User registerUser(User user);

    /**
     * remove user
     *
     * @param id
     * @return
     */
    void removeUser(Long id);

    /**
     * get user by id
     *
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     * search based on username and paging
     *
     * @param name
     * @return
     */
    Page<User> listUsersByNameLike(String name, Pageable pageable);

    /**
     * 根据用户名集合，查询用户详细信息列表
     * search list of users based on collection of username,
     *
     * @param usernames
     * @return
     */
    List<User> listUsersByUsernames(Collection<String> usernames);
}
