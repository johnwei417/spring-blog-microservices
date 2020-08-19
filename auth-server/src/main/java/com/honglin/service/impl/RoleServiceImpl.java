package com.honglin.service.impl;

import com.honglin.dao.RoleRepo;
import com.honglin.entity.Roles;
import com.honglin.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    public RoleServiceImpl(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public Roles getAuthorityById(Integer id) {
        return roleRepo.findById(id).get();
    }
}
