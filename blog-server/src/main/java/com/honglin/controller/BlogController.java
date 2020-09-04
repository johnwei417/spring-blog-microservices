package com.honglin.controller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.Blog;
import com.honglin.entity.Catalog;
import com.honglin.entity.User;
import com.honglin.entity.Vote;
import com.honglin.entity.es.EsBlog;
import com.honglin.service.*;
import com.honglin.vo.BlogDetailsVO;
import com.honglin.vo.BlogVO;
import com.honglin.vo.TagVO;
import com.honglin.vo.UserBlogListVO;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private EsBlogService esBlogService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private VoteService voteService;

    @GetMapping("/listBlogs")
    public CommonResponse listEsBlogs(
            @RequestParam(value = "order", required = false, defaultValue = "new") String order,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "async", required = false) boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        Page<EsBlog> page = null;
        List<EsBlog> list = null;
        boolean isEmpty = true; // initialize blog
        try {
            if (order.equals("hot")) { // hottest search
                Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "readSize", "commentSize", "voteSize", "createTime"));
                page = esBlogService.listHotestEsBlogs(keyword, pageable);
            } else if (order.equals("new")) { //search newest
                Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
                page = esBlogService.listNewestEsBlogs(keyword, pageable);
            }

            isEmpty = false;
        } catch (Exception e) {
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = esBlogService.listEsBlogs(pageable);
        }

        list = page.getContent();  //get current page data

//        only load when load page at first time
        if (!async && !isEmpty) {
            List<EsBlog> newest = esBlogService.listTop5NewestEsBlogs();

            List<EsBlog> hottest = esBlogService.listTop5HotestEsBlogs();

            List<TagVO> tags = esBlogService.listTop30Tags();

            List<User> users = esBlogService.listTop12Users();

            BlogVO blogVo = new BlogVO();
            blogVo.setHottest(hottest);
            blogVo.setNewest(newest);
            blogVo.setPage(list);
            blogVo.setTags(tags);
            blogVo.setUsers(users);
            return new CommonResponse(HttpStatus.SC_OK, "query blogs success!", blogVo);
        }

        return new CommonResponse<>(HttpStatus.SC_OK, "query blogs success!", list);
    }

    /**
     * get user's blog list
     *
     * @param username
     * @param order
     * @param catalogId
     * @param keyword
     * @param async
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("/{username}/blogs")
    public CommonResponse listBlogsByOrder(@PathVariable("username") String username,
                                           @RequestParam(value = "order", required = false, defaultValue = "new") String order,
                                           @RequestParam(value = "catalog", required = false) Long catalogId,
                                           @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                           @RequestParam(value = "async", required = false) boolean async,
                                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {

        User user = userService.findUserByUsername(username);

        Page<Blog> page = null;

        if (catalogId != null && catalogId > 0) { // search by catalog
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByCatalog(catalog, pageable);
            order = "";
        } else if (order.equals("hot")) { // search hottest
            Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "readSize", "commentSize", "voteSize"));
            page = blogService.listBlogsByTitleVoteAndSort(user, keyword, pageable);
        } else if (order.equals("new")) { // search for newest
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByTitleVote(user, keyword, pageable);
        }


        List<Blog> list = page.getContent();    // get content of current page

        UserBlogListVO userBlogListVO = new UserBlogListVO();
        userBlogListVO.setUser(user);
        userBlogListVO.setOrder(order);
        userBlogListVO.setKeyword(keyword);
        userBlogListVO.setCatalogId(catalogId);
        userBlogListVO.setList(list);
        return new CommonResponse(HttpStatus.SC_OK, "get user's blog list success", userBlogListVO);
    }

    /**
     * get blog detail page
     *
     * @param username
     * @param id
     * @param principal
     * @return
     */
    @GetMapping("/{username}/blogs/{id}")
    public CommonResponse getBlogById(@PathVariable("username") String username, @PathVariable("id") Long id, Principal principal) {
        Optional<Blog> blog;
        try {
            blog = Optional.of(blogService.getBlogById(id));
        } catch (NullPointerException e) {
            return new CommonResponse(HttpStatus.SC_BAD_REQUEST, "Blog id: " + id + " not exist");
        }

        //increase the read counter by 1 for each loading
        blogService.readingIncrease(id);

        //if user is owner of blog
        boolean isBlogOwner = false;

        if (principal != null && username.equals(principal.getName())) {
            isBlogOwner = true;
        }

        List<Vote> votes = blog.get().getVotes();
        Vote currentVote = null;
        //check if current user voted or not;
        if (principal != null) {
            for (Vote vote : votes) {
                if (vote.getUser().getUsername().equals(principal.getName())) {
                    currentVote = vote;
                    break;
                }
            }
        }

        BlogDetailsVO blogDetailsVO = new BlogDetailsVO();
        blogDetailsVO.setBlog(blog.get());
        blogDetailsVO.setCurrentVote(currentVote);
        blogDetailsVO.setIsBlogOwner(isBlogOwner);

        return new CommonResponse(HttpStatus.SC_OK, "get blog details success!", blogDetailsVO);
    }


}
