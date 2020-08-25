package com.honglin.service;

import com.honglin.entity.User;
import com.honglin.entity.es.EsBlog;
import com.honglin.vo.TagVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EsBlogService {

    /**
     * remove EsBlog
     *
     * @param id
     * @return
     */
    void removeEsBlog(String id);

    /**
     * update EsBlog
     *
     * @param esBlog
     * @return
     */
    EsBlog updateEsBlog(EsBlog esBlog);

    /**
     * get EsBlog by blog id
     *
     * @param blogId
     * @return
     */
    EsBlog getEsBlogByBlogId(Long blogId);

    /**
     * show newest blog list and paging
     *
     * @param keyword
     * @param pageable
     * @return
     */
    Page<EsBlog> listNewestEsBlogs(String keyword, Pageable pageable);

    /**
     * hottest blog list and paging
     *
     * @param keyword
     * @param pageable
     * @return
     */
    Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable);

    /**
     * list of blogs and paging
     *
     * @param pageable
     * @return
     */
    Page<EsBlog> listEsBlogs(Pageable pageable);

    /**
     * newest top 5
     *
     * @param
     * @return
     */
    List<EsBlog> listTop5NewestEsBlogs();

    /**
     * hottest top 5
     *
     * @param
     * @return
     */
    List<EsBlog> listTop5HotestEsBlogs();

    /**
     * hottest top 30 tag
     *
     * @return
     */
    List<TagVO> listTop30Tags();

    /**
     * hottest top 20 users
     *
     * @return
     */
    List<User> listTop12Users();
}

