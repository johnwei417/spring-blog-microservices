package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.Catalog;
import com.honglin.entity.User;
import com.honglin.service.CatalogService;
import com.honglin.service.UserService;
import com.honglin.vo.CatalogListVO;
import com.honglin.vo.CatalogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/catalogs")
@Slf4j
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private UserService userService;

    /**
     * get list of catalog
     *
     * @param username
     * @param
     * @return
     */
    @GetMapping
    public CommonResponse listCatalog(@RequestParam(value = "username", required = true) String username, Principal principal) {
        User user = (User) userService.findUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);
        boolean isOwner = false;
        try {
            Optional<Principal> isLogin = Optional.of(principal);
            if (isLogin.isPresent() && user.getUsername().equals(isLogin.get().getName())) {
                isOwner = true;
            }
        } catch (NullPointerException e) {
            log.info("Unknown user trying to get list of catalog");
        }

        CatalogListVO catalogListVO = new CatalogListVO();
        catalogListVO.setOwner(isOwner);
        catalogListVO.setCatalogs(catalogs);

        return new CommonResponse(HttpStatus.SC_OK, "query list of comments success!", catalogListVO);
    }

    /**
     * create catalog
     *
     * @param
     * @return
     */
    @PostMapping
    @PreAuthorize("authentication.name.equals(#catalogVO.username)")
    public CommonResponse create(@RequestBody CatalogVO catalogVO) {

        String username = catalogVO.getUsername();
        Catalog catalog = catalogVO.getCatalog();

        User user = (User) userService.findUserByUsername(username);

        try {
            catalog.setUser(user);
            catalogService.saveCatalog(catalog);
        } catch (ConstraintViolationException e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new CommonResponse(HttpStatus.SC_OK, "create catalog success!");
    }

    /**
     * remove catalog
     *
     * @param username
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public CommonResponse delete(String username, @PathVariable("id") Long id) {
        try {
            catalogService.removeCatalog(id);
        } catch (ConstraintViolationException e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new CommonResponse(HttpStatus.SC_OK, "remove catalog success!");
    }


}

