package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.Blog;
import com.honglin.entity.Catalog;
import com.honglin.entity.User;
import com.honglin.service.BlogService;
import com.honglin.service.CatalogService;
import com.honglin.service.UserService;
import com.honglin.vo.EditBlogVO;
import com.honglin.vo.newBlogVO;
import org.apache.http.HttpStatus;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
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


    @Value("${file.server.url}")
    private String fileServerUrl;

    /**
     * get data ready for creating new blog
     *
     * @param
     * @return
     */
    @GetMapping("/{username}/blogs/edit")
    public CommonResponse createBlog(@PathVariable("username") String username) {
        // get user's catalog list
        User user = (User) userService.findUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        newBlogVO blogvo = new newBlogVO();
        blogvo.setCatalogs(catalogs);
        blogvo.setFileServerUrl(fileServerUrl);
        return new CommonResponse(HttpStatus.SC_OK, "load data for create blog success!", blogvo);
    }

    /**
     * load data for editing blog
     *
     * @param
     * @return
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    public CommonResponse editBlog(@PathVariable("username") String username, @PathVariable("id") Long id) {
        // get user's catalog list
        User user = (User) userService.findUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        Blog blog = blogService.getBlogById(id);
        EditBlogVO editBlogVO = new EditBlogVO();
        editBlogVO.setBlog(blog);
        editBlogVO.setCatalogs(catalogs);
        editBlogVO.setFileServerUrl(fileServerUrl);

        return new CommonResponse(HttpStatus.SC_OK, "load data for edit blog success!", editBlogVO);
    }

    /**
     * save new/edit blog
     *
     * @param username
     * @param blog
     * @param catalogId
     * @return
     */
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public CommonResponse saveBlog(@PathVariable("username") String username,
                                   @RequestBody Blog blog,
                                   @RequestParam(required = false) Long catalogId) {

        try {
            //check whether it is edit or create new
            if (blog.getId() != null) {
                Blog orignalBlog = blogService.getBlogById(blog.getId());
                orignalBlog.setTitle(blog.getTitle());
                orignalBlog.setContent(blog.getContent());
                orignalBlog.setSummary(blog.getSummary());
                if (catalogId != null) {
                    Catalog catalog = catalogService.getCatalogById(catalogId);
                    orignalBlog.setCatalog(catalog);
                }
                orignalBlog.setTags(blog.getTags());
                blogService.saveBlog(orignalBlog);
            } else {
                User user = (User) userService.findUserByUsername(username);
                if (catalogId != null) {
                    Catalog catalog = catalogService.getCatalogById(catalogId);
                    blog.setCatalog(catalog);
                }
                blog.setUser(user);
                blogService.saveBlog(blog);
            }

        } catch (ConstraintViolationException e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new CommonResponse(HttpStatus.SC_OK, "Blog saved!");
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
