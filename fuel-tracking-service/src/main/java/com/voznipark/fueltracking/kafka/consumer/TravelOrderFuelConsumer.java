package com.voznipark.fueltracking.kafka.consumer;

import com.voznipark.fueltracking.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.fueltracking.service.FuelRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TravelOrderFuelConsumer {

    private final FuelRecordService fuelRecordService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "travel-order.completed", groupId = "fuel-tracking-service")
    public void onTravelOrderCompleted(String message) {
        try {
            TravelOrderCompletedEvent event = objectMapper.readValue(message, TravelOrderCompletedEvent.class);
            log.debug("Received TravelOrderCompletedEvent for travelOrderId={}", event.getTravelOrderId());
            fuelRecordService.processCompletedTravelOrder(event);
        } catch (Exception e) {
            log.error("Failed to process TravelOrderCompletedEvent: {}", e.getMessage(), e);
        }
    }
}