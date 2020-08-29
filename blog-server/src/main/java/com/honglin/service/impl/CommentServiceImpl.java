package com.honglin.service.impl;

import com.honglin.dao.CommentRepository;
import com.honglin.entity.Comment;
import com.honglin.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).get();
    }

    @Override
    public void removeComment(Long id) {
        commentRepository.deleteById(id);
    }

}
