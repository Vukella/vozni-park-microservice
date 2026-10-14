package com.voznipark.travelorder.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic travelOrderRequestedTopic() {
        return TopicBuilder.name("travel-order.requested")
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic travelOrderCompletedTopic() {
        return TopicBuilder.name("travel-order.completed")
                .partitions(1).replicas(1).build();
    }
}