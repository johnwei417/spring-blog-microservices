package com.honglin.contoller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.User;
import com.honglin.service.UserService;
import com.honglin.vo.ProfileVO;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Value("${file.server.url}")
    private String fileServerUrl;

    @Autowired
    private UserService userService;

    /**
     * users' profile page
     *
     * @param username
     * @param
     * @return
     */
    @GetMapping("/global/{username}")
    public CommonResponse userSpace(@PathVariable("username") String username) {
        try {
            Optional<User> user = Optional.of(userService.findUserByUsername(username));
        } catch (NullPointerException ex) {
            return new CommonResponse(HttpStatus.SC_NOT_FOUND, username + " not exist!");
        }
        return new CommonResponse(HttpStatus.SC_OK, "get " + username + " profile info success!");
    }

    /**
     * get personal profile setting page
     *
     * @param username
     * @param
     * @return
     */
    @GetMapping("/u/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public CommonResponse profile(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        ProfileVO profileVO = new ProfileVO();
        profileVO.setFileServerUrl(fileServerUrl);
        profileVO.setUser(user);
        return new CommonResponse(HttpStatus.SC_OK, "get personal profile info!", profileVO);
    }

    /**
     * save profile
     *
     * @param username
     * @param user
     * @return
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public CommonResponse saveProfile(@PathVariable("username") String username, @RequestBody User user) {
        User originalUser = userService.getUserById(user.getId());
        originalUser.setEmail(user.getEmail());
        originalUser.setFirstname(user.getFirstname());
        originalUser.setLastname(user.getLastname());

        userService.saveOrUpateUser(originalUser);
        return new CommonResponse(HttpStatus.SC_OK, "profile save success!");
    }

    /**
     * load avatar info
     *
     * @param username
     * @param
     * @return
     */
    @GetMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public CommonResponse avatar(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        String avatarUrl = user.getAvatar();
        return new CommonResponse(HttpStatus.SC_OK, "load avatar info success", avatarUrl);
    }

    /**
     * save avatar
     *
     * @param username
     * @param
     * @return
     */
    @PostMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public CommonResponse saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
        String avatarUrl = user.getAvatar();
        User originalUser = userService.findUserByUsername(username);
        originalUser.setAvatar(avatarUrl);
        userService.saveOrUpateUser(originalUser);

        return new CommonResponse(HttpStatus.SC_OK, "save avatar success!", avatarUrl);
    }


}
