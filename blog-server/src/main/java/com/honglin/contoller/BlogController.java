package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.User;
import com.honglin.entity.es.EsBlog;
import com.honglin.service.EsBlogService;
import com.honglin.vo.BlogVO;
import com.honglin.vo.TagVO;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private EsBlogService esBlogService;

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
}
