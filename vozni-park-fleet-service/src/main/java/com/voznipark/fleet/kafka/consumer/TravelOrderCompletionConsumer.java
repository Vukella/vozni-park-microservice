package com.voznipark.fleet.kafka.consumer;

import tools.jackson.databind.ObjectMapper;
import com.voznipark.fleet.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.fleet.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TravelOrderCompletionConsumer {

    private final ObjectMapper objectMapper;
    private final ReservationService reservationService;

    @KafkaListener(topics = "travel-order.completed", groupId = "fleet-service")
    public void handleTravelOrderCompleted(String message) {
        TravelOrderCompletedEvent event;
        try {
            event = objectMapper.readValue(message, TravelOrderCompletedEvent.class);
        } catch (Exception e) {
            log.error("Fleet: Failed to deserialize TravelOrderCompletedEvent: {}", e.getMessage());
            return;
        }

        log.info("Fleet: Received TravelOrderCompletedEvent — travelOrderId={}", event.getTravelOrderId());

        try {
            reservationService.releaseForTravelOrder(event.getTravelOrderId());
        } catch (Exception e) {
            log.error("Fleet: Failed to release reservations for travelOrderId={}: {}",
                    event.getTravelOrderId(), e.getMessage());
        }
    }
}