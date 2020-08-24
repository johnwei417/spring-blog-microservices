package com.honglin.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Autowired
    private Globals globals;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
//		factory.setConcurrency(10);
        factory.getContainerProperties().setPollTimeout(1500);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs());
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, globals.getServers());
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, globals.isEnableAutoCommit());
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, globals.getAutoCommitInterval());
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, globals.getKeyDeserializer());
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, globals.getValDeserializer());
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, globals.getGroupId());
        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, globals.getMaxPollRecords());
        return propsMap;
    }

}