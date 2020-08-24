package com.honglin.service;

import com.honglin.exceptions.KafkaFailureException;
import com.honglin.vo.EmailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Component
public class KafkaSender {

    @Resource
    private KafkaTemplate<String, EmailDto> kafkaTemplate;


    public void send(EmailDto emailDto) throws KafkaFailureException {
        log.info("send message,Message content : {}", emailDto);
        try {
            String uuid = UUID.randomUUID().toString();
            String topic = "register";

            ListenableFuture listenableFuture = kafkaTemplate.send(topic, uuid, emailDto);

            //Callback after sending successfully
            SuccessCallback<SendResult<String, String>> successCallback = new SuccessCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info("Message sent successfully");
                }
            };
            //Send failed callback
            FailureCallback failureCallback = new FailureCallback() {
                @Override
                public void onFailure(Throwable ex) {

                    log.error("Failed to send message", ex);
                    throw new KafkaFailureException("Failed to send message to kafka");
                }
            };

            listenableFuture.addCallback(successCallback, failureCallback);
        } catch (Exception e) {
            log.error("Sending message exception", e);
            throw new KafkaFailureException("Failed to send message to kafka");
        }
    }
}
