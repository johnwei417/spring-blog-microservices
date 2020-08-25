package com.honglin.vo;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class UserDto {
    private Integer id;

    @NotNull(message = "username cannot be null")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be blank")
//    @ValidPassword
    private String password;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @Email(message = "format of email is not correct")
    @NotNull
    @NotBlank
    private String email;

    private String avatar;

}
