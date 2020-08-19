package com.honglin.vo;


import lombok.Data;

@Data
public class TokenInfo {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
}
