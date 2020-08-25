package com.honglin.vo;

import com.honglin.entity.User;
import com.honglin.entity.es.EsBlog;
import lombok.Data;

import java.util.List;

@Data
public class BlogVO {

    List<EsBlog> newest;
    List<EsBlog> hottest;
    List<TagVO> tags;
    List<User> users;
    List<EsBlog> page;

}
