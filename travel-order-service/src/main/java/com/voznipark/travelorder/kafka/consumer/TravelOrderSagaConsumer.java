package com.voznipark.travelorder.kafka.consumer;

import tools.jackson.databind.ObjectMapper;
import com.voznipark.travelorder.kafka.event.DriverReservationFailedEvent;
import com.voznipark.travelorder.kafka.event.DriversReservedEvent;
import com.voznipark.travelorder.kafka.event.VehicleReleasedEvent;
import com.voznipark.travelorder.kafka.event.VehicleReservationFailedEvent;
import com.voznipark.travelorder.kafka.event.VehicleReservedEvent;
import com.voznipark.travelorder.service.TravelOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens for Saga response events from fleet-service and updates TravelOrder
 * status and sagaStatus accordingly.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TravelOrderSagaConsumer {

    private final ObjectMapper objectMapper;
    private final TravelOrderService travelOrderService;

    /** Saga SUCCESS — vehicle and drivers reserved. Confirms the travel order. */
    @KafkaListener(topics = "fleet.drivers.reserved", groupId = "travel-order-service")
    public void handleDriversReserved(String message) {
        try {
            DriversReservedEvent event = objectMapper.readValue(message, DriversReservedEvent.class);
            log.info("Saga [travelOrder={}]: DriversReservedEvent received — confirming order",
                    event.getTravelOrderId());
            travelOrderService.confirmTravelOrder(event.getTravelOrderId());
        } catch (Exception e) {
            log.error("Saga: Failed to process DriversReservedEvent: {}", e.getMessage(), e);
        }
    }

    /** snapshots vehicleSapNumber onto the order for the completion event */
    @KafkaListener(topics = "fleet.vehicle.reserved", groupId = "travel-order-service")
    public void handleVehicleReserved(String message) {
        try {
            VehicleReservedEvent event = objectMapper.readValue(message, VehicleReservedEvent.class);
            log.info("Saga [travelOrder={}]: VehicleReservedEvent received — sagaStatus → VEHICLE_RESERVED, vehicleSapNumber={}",
                    event.getTravelOrderId(), event.getVehicleSapNumber());
            travelOrderService.markVehicleReserved(event.getTravelOrderId(), event.getVehicleSapNumber());
        } catch (Exception e) {
            log.error("Saga: Failed to process VehicleReservedEvent: {}", e.getMessage(), e);
        }
    }

    /** Vehicle not available — no compensation needed, order fails immediately. */
    @KafkaListener(topics = "fleet.vehicle.reservation-failed", groupId = "travel-order-service")
    public void handleVehicleReservationFailed(String message) {
        try {
            VehicleReservationFailedEvent event = objectMapper.readValue(message, VehicleReservationFailedEvent.class);
            log.warn("Saga [travelOrder={}]: VehicleReservationFailedEvent — vehicle {} not available: {}",
                    event.getTravelOrderId(), event.getVehicleId(), event.getReason());
            travelOrderService.failTravelOrder(
                    event.getTravelOrderId(),
                    "Vehicle " + event.getVehicleId() + " not available: " + event.getReason());
        } catch (Exception e) {
            log.error("Saga: Failed to process VehicleReservationFailedEvent: {}", e.getMessage(), e);
        }
    }

    /** Drivers not available — vehicle was already released (compensation complete), order fails. */
    @KafkaListener(topics = "fleet.driver.reservation-failed", groupId = "travel-order-service")
    public void handleDriverReservationFailed(String message) {
        try {
            DriverReservationFailedEvent event = objectMapper.readValue(message, DriverReservationFailedEvent.class);
            log.warn("Saga [travelOrder={}]: DriverReservationFailedEvent — drivers {} not available: {}",
                    event.getTravelOrderId(), event.getRequestedDriverIds(), event.getReason());
            travelOrderService.failTravelOrder(
                    event.getTravelOrderId(),
                    "Driver reservation failed (vehicle released as compensation): " + event.getReason());
        } catch (Exception e) {
            log.error("Saga: Failed to process DriverReservationFailedEvent: {}", e.getMessage(), e);
        }
    }

    /** Compensation acknowledgement — vehicle released back to fleet. Logged only. */
    @KafkaListener(topics = "fleet.vehicle.released", groupId = "travel-order-service")
    public void handleVehicleReleased(String message) {
        try {
            VehicleReleasedEvent event = objectMapper.readValue(message, VehicleReleasedEvent.class);
            log.info("Saga [travelOrder={}]: VehicleReleasedEvent — vehicle {} released: {}",
                    event.getTravelOrderId(), event.getVehicleId(), event.getReason());
            // No state change here — failTravelOrder() is called by handleDriverReservationFailed()
        } catch (Exception e) {
            log.error("Saga: Failed to process VehicleReleasedEvent: {}", e.getMessage(), e);
        }
    }
}