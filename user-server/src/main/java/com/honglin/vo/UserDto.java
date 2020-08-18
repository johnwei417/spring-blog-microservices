package com.honglin.vo;

import com.honglin.common.constraint.ValidPassword;
import com.honglin.entity.Roles;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private Integer id;

    @NotNull(message = "username cannot be null")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be blank")
    @ValidPassword
    private String password;

    private String firstname;

    private String lastname;

    @Email
    private String email;

    private List<Roles> authorities;

    private Date createDate;
}
