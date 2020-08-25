package com.honglin.vo;

import com.honglin.entity.Catalog;
import lombok.Data;

import java.util.List;

@Data
public class CatalogListVO {
    private boolean isOwner;
    private List<Catalog> catalogs;
}
