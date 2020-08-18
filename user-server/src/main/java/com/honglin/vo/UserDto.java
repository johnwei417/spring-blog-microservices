package com.honglin.vo;

import com.honglin.entity.Roles;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private Integer id;


    private String username;


    private String password;


    private String firstname;


    private String lastname;


    private String email;

    private List<Roles> authorities;

    private Date createDate;
}
