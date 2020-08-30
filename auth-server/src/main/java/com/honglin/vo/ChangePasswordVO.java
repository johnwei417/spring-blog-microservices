package com.honglin.vo;

import lombok.Data;

@Data
public class ChangePasswordVO {
    private String oldPassword;
    private String newPassword;
}
