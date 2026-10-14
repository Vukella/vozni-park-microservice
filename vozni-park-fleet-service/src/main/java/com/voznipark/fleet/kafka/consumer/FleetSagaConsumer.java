package com.voznipark.fleet.kafka.consumer;

import tools.jackson.databind.ObjectMapper;
import com.voznipark.fleet.exception.DriverNotAvailableException;
import com.voznipark.fleet.exception.VehicleNotAvailableException;
import com.voznipark.fleet.kafka.event.DriverReservationFailedEvent;
import com.voznipark.fleet.kafka.event.DriversReservedEvent;
import com.voznipark.fleet.kafka.event.TravelOrderRequestedEvent;
import com.voznipark.fleet.kafka.event.VehicleReleasedEvent;
import com.voznipark.fleet.kafka.event.VehicleReservationFailedEvent;
import com.voznipark.fleet.kafka.event.VehicleReservedEvent;
import com.voznipark.fleet.kafka.producer.FleetEventProducer;
import com.voznipark.fleet.service.DriverService;
import com.voznipark.fleet.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Saga participant: listens for TravelOrderRequestedEvent, reserves vehicle then
 * drivers, publishes success or failure events. Compensation on driver failure
 * releases the already-reserved vehicle.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FleetSagaConsumer {

    private final ObjectMapper objectMapper;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final FleetEventProducer fleetEventProducer;

    @KafkaListener(topics = "travel-order.requested", groupId = "fleet-service")
    public void handleTravelOrderRequested(String message) {
        TravelOrderRequestedEvent event;
        try {
            event = objectMapper.readValue(message, TravelOrderRequestedEvent.class);
        } catch (Exception e) {
            log.error("Saga: Failed to deserialize TravelOrderRequestedEvent: {}", e.getMessage());
            return;
        }

        log.info("Saga [travelOrder={}]: Received TravelOrderRequestedEvent — vehicle={}, drivers={}",
                event.getTravelOrderId(), event.getVehicleId(), event.getDriverIds());

        // Step 1: Reserve vehicle
        // SAP number returned so travel-order-service can snapshot it without an extra lookup
        Integer vehicleSapNumber;
        try {
            vehicleSapNumber = vehicleService.reserveVehicle(event.getVehicleId(), event.getTravelOrderId());
        } catch (VehicleNotAvailableException | com.voznipark.fleet.exception.EntityNotFoundException e) {
            log.warn("Saga [travelOrder={}]: Vehicle {} not available — {}",
                    event.getTravelOrderId(), event.getVehicleId(), e.getMessage());
            publishSafely(() -> fleetEventProducer.publishVehicleReservationFailed(
                    VehicleReservationFailedEvent.builder()
                            .travelOrderId(event.getTravelOrderId())
                            .vehicleId(event.getVehicleId())
                            .reason(e.getMessage())
                            .occurredAt(Instant.now())
                            .build()));
            return;
        }

        log.info("Saga [travelOrder={}]: Vehicle {} reserved (sapNumber={})",
                event.getTravelOrderId(), event.getVehicleId(), vehicleSapNumber);

        publishSafely(() -> fleetEventProducer.publishVehicleReserved(
                VehicleReservedEvent.builder()
                        .travelOrderId(event.getTravelOrderId())
                        .vehicleId(event.getVehicleId())
                        .vehicleSapNumber(vehicleSapNumber)
                        .driverIds(event.getDriverIds())
                        .occurredAt(Instant.now())
                        .build()));

        // Step 2: Reserve drivers
        try {
            driverService.reserveDrivers(event.getDriverIds(), event.getTravelOrderId());
        } catch (DriverNotAvailableException e) {
            log.warn("Saga [travelOrder={}]: Driver reservation failed — {}. Running compensation.",
                    event.getTravelOrderId(), e.getMessage());

            vehicleService.releaseVehicle(event.getVehicleId(), event.getTravelOrderId());

            publishSafely(() -> fleetEventProducer.publishVehicleReleased(
                    VehicleReleasedEvent.builder()
                            .travelOrderId(event.getTravelOrderId())
                            .vehicleId(event.getVehicleId())
                            .reason("Driver reservation failed — compensating transaction")
                            .occurredAt(Instant.now())
                            .build()));

            publishSafely(() -> fleetEventProducer.publishDriverReservationFailed(
                    DriverReservationFailedEvent.builder()
                            .travelOrderId(event.getTravelOrderId())
                            .vehicleId(event.getVehicleId())
                            .requestedDriverIds(event.getDriverIds())
                            .reason(e.getMessage())
                            .occurredAt(Instant.now())
                            .build()));
            return;
        }

        // Both reserved: Saga success
        log.info("Saga [travelOrder={}]: Drivers {} reserved — publishing success",
                event.getTravelOrderId(), event.getDriverIds());
        publishSafely(() -> fleetEventProducer.publishDriversReserved(
                DriversReservedEvent.builder()
                        .travelOrderId(event.getTravelOrderId())
                        .vehicleId(event.getVehicleId())
                        .driverIds(event.getDriverIds())
                        .occurredAt(Instant.now())
                        .build()));

        log.info("Saga [travelOrder={}]: COMPLETE — vehicle {} and drivers {} reserved successfully",
                event.getTravelOrderId(), event.getVehicleId(), event.getDriverIds());
    }

    private void publishSafely(Runnable publishAction) {
        try {
            publishAction.run();
        } catch (Exception e) {
            log.error("Saga: Failed to publish event: {}", e.getMessage());
        }
    }
}