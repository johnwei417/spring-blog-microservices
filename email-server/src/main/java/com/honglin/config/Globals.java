package com.honglin.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class Globals {

    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;

    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private String autoCommitInterval;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.listener.concurrency:1}")
    private int concurrency;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private int maxPollRecords;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valDeserializer;

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

}
