package com.voznipark.fleet.kafka.config;

import com.voznipark.fleet.kafka.topics.FleetTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

// broker has auto.create.topics.enable=false, so every topic must be declared here
@Configuration
public class KafkaTopicConfig {

    // Vehicle topics

    @Bean
    public NewTopic vehicleStatusChangedTopic() {
        return TopicBuilder.name(FleetTopics.VEHICLE_STATUS_CHANGED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic vehicleFaultReportedTopic() {
        return TopicBuilder.name(FleetTopics.VEHICLE_FAULT_REPORTED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic vehicleFaultResolvedTopic() {
        return TopicBuilder.name(FleetTopics.VEHICLE_FAULT_RESOLVED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic vehicleServiceScheduledTopic() {
        return TopicBuilder.name(FleetTopics.VEHICLE_SERVICE_SCHEDULED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic vehicleDecommissionedTopic() {
        return TopicBuilder.name(FleetTopics.VEHICLE_DECOMMISSIONED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic vehicleDocumentUploadedTopic() {
        return TopicBuilder.name(FleetTopics.VEHICLE_DOCUMENT_UPLOADED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic registrationNumberChangedTopic() {
        return TopicBuilder.name(FleetTopics.REGISTRATION_NUMBER_CHANGED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic vehicleAssignedDriverChangedTopic() {
        return TopicBuilder.name(FleetTopics.VEHICLE_ASSIGNED_DRIVER_CHANGED)
                .partitions(1).replicas(1).build();
    }

    // Driver topics

    @Bean
    public NewTopic driverStatusChangedTopic() {
        return TopicBuilder.name(FleetTopics.DRIVER_STATUS_CHANGED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic driverLicenseRevokedTopic() {
        return TopicBuilder.name(FleetTopics.DRIVER_LICENSE_REVOKED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic driverLicenseRestoredTopic() {
        return TopicBuilder.name(FleetTopics.DRIVER_LICENSE_RESTORED)
                .partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic trafficAccidentReportedTopic() {
        return TopicBuilder.name(FleetTopics.TRAFFIC_ACCIDENT_REPORTED)
                .partitions(1).replicas(1).build();
    }

    // Shared topics

    @Bean
    public NewTopic documentExpiringTopic() {
        return TopicBuilder.name(FleetTopics.DOCUMENT_EXPIRING)
                .partitions(1).replicas(1).build();
    }
}
