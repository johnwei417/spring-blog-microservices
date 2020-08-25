package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.User;
import com.honglin.service.BlogService;
import com.honglin.service.VoteService;
import org.apache.http.HttpStatus;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/votes")
public class VoteController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private VoteService voteService;

    /**
     * create vote (like)
     *
     * @param blogId
     * @param blogId
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")  // role based
    public CommonResponse createVote(Long blogId) {

        try {
            blogService.createVote(blogId);
        } catch (ConstraintViolationException e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new CommonResponse(HttpStatus.SC_OK, "vote success!");
    }

    /**
     * remove vote (unlike)
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public CommonResponse delete(@PathVariable("id") Long id, Long blogId, Principal principal) {

        boolean isOwner = false;
        User user = voteService.getVoteById(id).getUser();
//        check if user is valid
        if (principal != null && user.getUsername().equals(principal.getName())) {
            isOwner = true;
        }

        if (!isOwner) {
            return new CommonResponse(HttpStatus.SC_UNAUTHORIZED, "unauthorized operation");
        }

        try {
            blogService.removeVote(blogId, id);
            voteService.removeVote(id);
        } catch (ConstraintViolationException e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new CommonResponse(HttpStatus.SC_OK, "cancel vote success!");

    }
}
