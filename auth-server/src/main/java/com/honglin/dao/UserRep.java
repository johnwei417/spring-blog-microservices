package com.honglin.dao;

import com.honglin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface UserRep extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    Integer deleteByUsername(String username);

    @Lock(value = LockModeType.OPTIMISTIC)
    User save(User user);
}
