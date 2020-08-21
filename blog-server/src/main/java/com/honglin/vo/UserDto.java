package com.honglin.vo;

import lombok.Data;

@Data
public class UserDto {

    private String username;

    private String firstname;

    private String lastname;

    private byte[] avatar;

    private String email;

    private String profile;
}
