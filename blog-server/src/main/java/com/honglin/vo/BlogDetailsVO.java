package com.honglin.vo;

import com.honglin.entity.Blog;
import com.honglin.entity.Vote;
import lombok.Data;

@Data
public class BlogDetailsVO {
    private Vote currentVote;
    private Boolean isBlogOwner;
    private Blog blog;
}
