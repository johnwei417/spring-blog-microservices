package com.honglin.vo;

import com.honglin.entity.Blog;
import com.honglin.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserBlogListVO {
    private User user;
    private String order;
    private String keyword;
    private Long catalogId;
    private List<Blog> list;
}
