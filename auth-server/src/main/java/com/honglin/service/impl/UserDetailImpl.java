package com.honglin.service.impl;

import com.honglin.dao.UserRep;
import com.honglin.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class UserDetailImpl implements UserDetailsService {

    private final UserRep userRep;

    public UserDetailImpl(UserRep userRep) {
        this.userRep = userRep;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRep.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return user;
    }

}
