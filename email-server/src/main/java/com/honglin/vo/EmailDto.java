package com.honglin.vo;

import lombok.Data;

import java.util.Map;

@Data
public class EmailDto {
    private String subject;
    private String from;
    private String mailTo;
    private Map<String, Object> props;
}
