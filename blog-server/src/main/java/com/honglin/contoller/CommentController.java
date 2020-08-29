package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.Blog;
import com.honglin.entity.Comment;
import com.honglin.entity.User;
import com.honglin.service.BlogService;
import com.honglin.service.CommentService;
import com.honglin.vo.CommentListVO;
import org.apache.http.HttpStatus;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    /**
     * get list of comments
     *
     * @param blogId
     * @param
     * @return
     */
    @GetMapping("/getListOfComments")
    public CommonResponse<CommentListVO> listComments(@RequestParam(value = "blogId", required = true) Long blogId, Principal principal) {
        Optional<Blog> blog;
        try {
            blog = Optional.of(blogService.getBlogById(blogId));
        } catch (NullPointerException ex) {
            return new CommonResponse<>(HttpStatus.SC_BAD_REQUEST, "Blog is not exist!");
        }
        List<Comment> comments = blog.get().getComments();
        // check if is owner
        String commentOwner = "";
        if (principal != null) {
            commentOwner = principal.getName();
        }
        CommentListVO commentListVO = new CommentListVO();
        commentListVO.setCommentOwner(commentOwner);
        commentListVO.setComments(comments);
        return new CommonResponse(HttpStatus.SC_OK, "get list of comment success!", commentListVO);
    }

    /**
     * post comment
     *
     * @param blogId
     * @param commentContent
     * @return
     */
    @PostMapping("/createComment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public CommonResponse createComment(@RequestParam(value = "blogId", required = true) Long blogId,
                                        @RequestParam(value = "commentContent", required = true) String commentContent, Principal principal) {

        Optional<Principal> isLogin = Optional.of(principal);
        try {
            if (isLogin.isPresent()) {
                blogService.createComment(blogId, commentContent, isLogin.get());
            }
        } catch (ConstraintViolationException e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new CommonResponse(HttpStatus.SC_OK, "create comment success!");
    }

    /**
     * remove comments
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public CommonResponse delete(@PathVariable("id") Long id, Long blogId, Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = false;

        Optional<Principal> isLogin = Optional.of(principal);
        try {
            User user = commentService.getCommentById(id).getUser();
            if (isLogin.isPresent() && user.getUsername().equals(isLogin.get().getName())) {
                isOwner = true;
            }

            if (!isOwner && !hasAdminRole) {
                return new CommonResponse(HttpStatus.SC_UNAUTHORIZED, "UNAUTHORIZED");
            }

            try {
                blogService.removeComment(blogId, id);
                commentService.removeComment(id);
            } catch (ConstraintViolationException e) {
                return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (Exception e) {
                return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (NoSuchElementException e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "No value represent");
        }

        return new CommonResponse(HttpStatus.SC_OK, "delete comment success!");
    }
}
