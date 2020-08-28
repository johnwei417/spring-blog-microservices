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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
        Blog blog = blogService.getBlogById(blogId);
        List<Comment> comments = blog.getComments();

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

        try {
            blogService.createComment(blogId, commentContent, principal);
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

        boolean isOwner = false;
        User user = commentService.getCommentById(id).getUser();

        if (principal != null && user.getUsername().equals(principal.getName())) {
            isOwner = true;
        }

        if (!isOwner) {
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

        return new CommonResponse(HttpStatus.SC_OK, "delete comment success!");
    }
}
