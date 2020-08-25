package com.honglin.dao;

import com.honglin.entity.Catalog;
import com.honglin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    /**
     * find by username
     *
     * @param user
     * @return
     */
    List<Catalog> findByUser(User user);

    /**
     * find by user and catalog
     *
     * @param user
     * @param name
     * @return
     */
    List<Catalog> findByUserAndName(User user, String name);
}

