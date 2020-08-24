package com.honglin.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.honglin.vo.EmailDto;
import com.honglin.vo.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class KafkaBatchConsumer {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = {"${spring.kafka.template.default-topic}"},
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<ConsumerRecord> records, Acknowledgment ack) {
        log.info("start consume");
        try {
            for (ConsumerRecord<String, String> record : records) {
                ObjectMapper objectMapper = new ObjectMapper();
                log.info("receive message: offset = {}, key = {}, value = {} ",
                        record.offset(), record.key(), record.value());
                UserDto userDto = objectMapper.readValue(record.value(), UserDto.class);
                EmailDto emailDto = new EmailDto();
                emailDto.setFrom("hwei970417@gmail.com");
                emailDto.setMailTo(userDto.getEmail());
                emailDto.setSubject("Thanks for your register!");
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("name", userDto.getFirstname());
                emailDto.setProps(model);
                emailService.sendEmail(emailDto);
                log.info(userDto.getUsername() + ": " + userDto.getEmail());
            }
        } catch (Exception e) {
            log.error("kafka receiver get error", e);
        } finally {

            //submit ack offset
            ack.acknowledge();
        }
    }
}
