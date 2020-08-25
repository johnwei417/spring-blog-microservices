package com.honglin.service;

import com.honglin.entity.Comment;

public interface CommentService {

    /**
     * get Comment by id
     *
     * @param id
     * @return
     */
    Comment getCommentById(Long id);

    /**
     * delete comment
     *
     * @param id
     * @return
     */
    void removeComment(Long id);
}
