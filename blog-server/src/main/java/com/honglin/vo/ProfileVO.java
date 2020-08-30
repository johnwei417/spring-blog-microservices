package com.honglin.vo;

import com.honglin.entity.User;
import lombok.Data;

@Data
public class ProfileVO {
    private String fileServerUrl;
    private User user;
}
