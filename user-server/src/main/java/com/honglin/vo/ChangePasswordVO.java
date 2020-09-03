package com.honglin.vo;

import com.honglin.common.constraint.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordVO {
    @NotNull
    @NotEmpty(message = "old password cannot be empty")
    private String oldPassword;
    @ValidPassword
    @NotNull
    @NotEmpty(message = "new password cannot be empty")
    private String newPassword;
}