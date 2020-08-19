package com.honglin.dao;

import com.honglin.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Roles, Integer> {
}
