package com.honglin.service;

import com.honglin.entity.Catalog;
import com.honglin.entity.User;

import java.util.List;

public interface CatalogService {
    /**
     * save catalog
     *
     * @param catalog
     * @return
     */
    Catalog saveCatalog(Catalog catalog);

    /**
     * remove catalog
     *
     * @param id
     * @return
     */
    void removeCatalog(Long id);

    /**
     * get catalog by id
     *
     * @param id
     * @return
     */
    Catalog getCatalogById(Long id);

    /**
     * get list of catalog
     *
     * @return
     */
    List<Catalog> listCatalogs(User user);
}
