package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.Blog;
import com.honglin.entity.User;
import com.honglin.service.BlogService;
import com.honglin.service.CatalogService;
import com.honglin.service.UserService;
import org.apache.http.HttpStatus;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/blogEdit")
public class BlogEditorController {
    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CatalogService catalogService;


    @PostMapping("/createBlog")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public CommonResponse save(@RequestBody Blog blog, Principal principal) {
        Optional<Principal> isLogin = Optional.of(principal);
        if (isLogin.isPresent()) {
            if (principal.getName() != null) {
                User user = userService.findUserByUsername(isLogin.get().getName());
                blog.setUser(user);
                try {
                    blogService.saveBlog(blog);
                    return new CommonResponse(HttpStatus.SC_OK, "new blog created success!");
                } catch (ConstraintViolationException e) {
                    return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
        }
        return new CommonResponse(HttpStatus.SC_UNAUTHORIZED, "UNAUTHORIZED");
    }

    @PostMapping("/removeBlog")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public CommonResponse removeBlog(@RequestParam Long blogId, Principal principal) {
        Optional<Principal> isLogin = Optional.of(principal);
        if (isLogin.isPresent()) {
            //verify the people who want delete blog is the owner of this blog
            //and allow admin to delete blog as well
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            boolean hasAdminRole = authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
            Optional<Blog> deleteBlog;
            try {
                deleteBlog = Optional.of(blogService.getBlogById(blogId));
            } catch (NullPointerException ex) {
                return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "blog: " + blogId + " not exist");
            }
            if (deleteBlog.get().getUser().getUsername().equals(isLogin.get().getName()) || hasAdminRole) {
                try {
                    blogService.removeBlog(blogId);
                    return new CommonResponse(HttpStatus.SC_OK, "remove blog success!");
                } catch (ConstraintViolationException e) {
                    return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
        }
        return new CommonResponse(HttpStatus.SC_UNAUTHORIZED, "UNAUTHORIZED");
    }
}
