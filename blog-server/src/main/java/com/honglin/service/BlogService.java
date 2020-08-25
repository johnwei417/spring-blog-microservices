package com.honglin.service;

import com.honglin.entity.Blog;
import com.honglin.entity.Catalog;
import com.honglin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    /**
     * save blog
     *
     * @param blog
     * @return
     */
    Blog saveBlog(Blog blog);

    /**
     * remove blog
     *
     * @param id
     * @return
     */
    void removeBlog(Long id);

    /**
     * get blog by id
     *
     * @param id
     * @return
     */
    Blog getBlogById(Long id);

    /**
     * get list of blogs by title and vote (newest)
     *
     * @param user
     * @return
     */
    Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable);

    /**
     * get list of blogs by title and vote (hottest)
     *
     * @param user
     * @return
     */
    Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable);

    /**
     * increase count of reading
     *
     * @param id
     */
    void readingIncrease(Long id);

    /**
     * post comment
     *
     * @param blogId
     * @param commentContent
     * @return
     */
    Blog createComment(Long blogId, String commentContent);

    /**
     * remove comment
     *
     * @param blogId
     * @param commentId
     * @return
     */
    void removeComment(Long blogId, Long commentId);

    /**
     * vote for blog
     *
     * @param blogId
     * @return
     */
    Blog createVote(Long blogId);

    /**
     * cancel vote for blog
     *
     * @param blogId
     * @param voteId
     * @return
     */
    void removeVote(Long blogId, Long voteId);

    /**
     * get list of blogs by catalog
     *
     * @param catalog
     * @param pageable
     * @return
     */
    Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable);
}
