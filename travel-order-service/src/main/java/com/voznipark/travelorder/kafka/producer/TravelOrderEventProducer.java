package com.voznipark.travelorder.kafka.producer;

import tools.jackson.databind.ObjectMapper;
import com.voznipark.travelorder.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.travelorder.kafka.event.TravelOrderRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// same KafkaTemplate<String, String> + manual ObjectMapper pattern as FleetEventProducer
@Component
@RequiredArgsConstructor
@Slf4j
public class TravelOrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishTravelOrderRequested(TravelOrderRequestedEvent event) {
        publish("travel-order.requested", String.valueOf(event.getTravelOrderId()), event);
    }

    public void publishTravelOrderCompleted(TravelOrderCompletedEvent event) {
        publish("travel-order.completed", String.valueOf(event.getTravelOrderId()), event);
    }

    private void publish(String topic, String key, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, json);
            log.info("Published {} to topic '{}' with key '{}'",
                    event.getClass().getSimpleName(), topic, key);
        } catch (Exception e) {
            log.error("Failed to publish {} to topic '{}': {}",
                    event.getClass().getSimpleName(), topic, e.getMessage());
            throw new RuntimeException("Kafka publish failed for event " +
                    event.getClass().getSimpleName(), e);
        }
    }
}
