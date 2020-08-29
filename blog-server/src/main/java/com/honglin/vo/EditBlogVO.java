package com.honglin.vo;

import com.honglin.entity.Blog;
import com.honglin.entity.Catalog;
import lombok.Data;

import java.util.List;

@Data
public class EditBlogVO {
    private List<Catalog> catalogs;
    private String fileServerUrl;
    private Blog blog;

}
