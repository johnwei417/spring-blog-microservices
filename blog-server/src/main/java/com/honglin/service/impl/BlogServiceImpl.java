package com.honglin.service.impl;

import com.honglin.dao.BlogRepository;
import com.honglin.entity.*;
import com.honglin.entity.es.EsBlog;
import com.honglin.service.BlogService;
import com.honglin.service.EsBlogService;
import com.honglin.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;


@Service
public class BlogServiceImpl implements BlogService {

    private final EsBlogService esBlogService;

    private final BlogRepository blogRepository;

    private final UserService userService;

    public BlogServiceImpl(EsBlogService esBlogService, BlogRepository blogRepository, UserService userService) {
        this.esBlogService = esBlogService;
        this.blogRepository = blogRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        boolean isNew = (blog.getId() == null);
        EsBlog esBlog = null;

        Blog returnBlog = blogRepository.save(blog);

        if (isNew) {
            esBlog = new EsBlog(returnBlog);
        } else {
            esBlog = esBlogService.getEsBlogByBlogId(blog.getId());
            esBlog.update(returnBlog);
        }

        esBlogService.updateEsBlog(esBlog);
        return returnBlog;
    }

    @Transactional
    @Override
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
        EsBlog esblog = esBlogService.getEsBlogByBlogId(id);
        esBlogService.removeEsBlog(esblog.getId());
    }

    @Override
    public Blog getBlogById(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        return blog.isPresent() ? blog.get() : null;
    }

    @Override
    public Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable) {
        // search
        title = "%" + title + "%";
        String tags = title;
        Page<Blog> blogs = blogRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(title, user, tags, user, pageable);
        return blogs;
    }

    @Override
    public Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable) {
        // search
        title = "%" + title + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
        return blogs;
    }

    @Override
    public void readingIncrease(Long id) {
        Blog blog = blogRepository.findById(id).get();
        blog.setReadSize(blog.getReadSize() + 1); // increment by 1
        this.saveBlog(blog);
    }

    @Override
    public Blog createComment(Long blogId, String commentContent, Principal principal) {
        Blog originalBlog = blogRepository.findById(blogId).get();
        User user = userService.findUserByUsername(principal.getName());
        Comment comment = new Comment(user, commentContent);
        originalBlog.addComment(comment);
        return this.saveBlog(originalBlog);
    }

    @Override
    public void removeComment(Long blogId, Long commentId) {
        Blog originalBlog = blogRepository.findById(blogId).get();
        originalBlog.removeComment(commentId);
        this.saveBlog(originalBlog);
    }

    @Override
    public Blog createVote(Long blogId) {
        Blog originalBlog = blogRepository.findById(blogId).get();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Vote vote = new Vote(user);
        boolean isExist = originalBlog.addVote(vote);
        if (isExist) {
            throw new IllegalArgumentException("This user already voted!");
        }
        return this.saveBlog(originalBlog);
    }

    @Override
    public void removeVote(Long blogId, Long voteId) {
        Blog originalBlog = blogRepository.findById(blogId).get();
        originalBlog.removeVote(voteId);
        this.saveBlog(originalBlog);
    }

    @Override
    public Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable) {
        Page<Blog> blogs = blogRepository.findByCatalog(catalog, pageable);
        return blogs;
    }
}

