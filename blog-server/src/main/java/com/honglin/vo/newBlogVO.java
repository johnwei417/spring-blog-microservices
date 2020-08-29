package com.honglin.vo;

import com.honglin.entity.Catalog;
import lombok.Data;

import java.util.List;

@Data
public class newBlogVO {
    private List<Catalog> catalogs;
    private String fileServerUrl;
}
