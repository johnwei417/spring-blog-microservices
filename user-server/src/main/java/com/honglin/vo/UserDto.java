package com.honglin.vo;

import com.honglin.entity.Roles;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private Integer id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String firstname;

    private String lastname;

    @Email
    private String email;

    private List<Roles> authorities;

    private Date createDate;
}
