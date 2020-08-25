package com.honglin.dao;


import com.honglin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * find user by name and paging
     *
     * @param name
     * @param pageable
     * @return
     */
    Page<User> findByUsernameLike(String name, Pageable pageable);

    /**
     * search user by username
     *
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * find user from userlist
     *
     * @param usernames
     * @return
     */
    List<User> findByUsernameIn(Collection<String> usernames);
}
