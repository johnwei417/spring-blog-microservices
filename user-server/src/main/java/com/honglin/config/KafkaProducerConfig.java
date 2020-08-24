package com.honglin.config;

import com.honglin.vo.EmailDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Autowired
    private Globals globals;


    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, globals.getServers());
        props.put(ProducerConfig.RETRIES_CONFIG, globals.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, globals.getBatchSize());
        //props.put(ProducerConfig.LINGER_MS_CONFIG, globals.getLinger());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, globals.getBufferMemory());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, globals.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, globals.getValSerializer());
        return props;
    }

    @Bean
    public ProducerFactory<String, EmailDto> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, EmailDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
