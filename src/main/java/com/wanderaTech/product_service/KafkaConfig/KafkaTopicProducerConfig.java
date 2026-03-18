package com.wanderaTech.product_service.KafkaConfig;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicProducerConfig {

    //this creates kafka topic
    @Bean
    public NewTopic productTopic(){
        return TopicBuilder
                .name("initialStock-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

