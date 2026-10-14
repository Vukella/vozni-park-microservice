package com.voznipark.maintenance.kafka.consumer;

import com.voznipark.maintenance.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.maintenance.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumes TravelOrderCompleted events from travel-order-service.
 * Delegates mileage accumulation and threshold evaluation to MaintenanceService.
 *
 * Kept separate from FleetEventMaintenanceConsumer because the trigger source
 * (travel orders vs fleet events) and the processing logic are independent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TravelOrderMaintenanceConsumer {

    private final MaintenanceService maintenanceService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "travel-order.completed",
            groupId = "technical-maintenance-service"
    )
    public void handleTravelOrderCompleted(String message) {
        try {
            TravelOrderCompletedEvent event =
                    objectMapper.readValue(message, TravelOrderCompletedEvent.class);
            log.info("Received TravelOrderCompleted | travelOrderId={} vehicleId={} distanceKm={}",
                    event.getTravelOrderId(), event.getVehicleId(), event.getDistanceKm());
            maintenanceService.processTravelOrderCompleted(event);
        } catch (Exception e) {
            log.error("Failed to process TravelOrderCompleted | error={} payload={}",
                    e.getMessage(), message);
        }
    }
}
