package com.honglin.vo;

import com.honglin.entity.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentListVO {
    private String commentOwner;
    private List<Comment> comments;
}
