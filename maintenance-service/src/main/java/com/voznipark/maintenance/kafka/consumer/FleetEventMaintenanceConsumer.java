package com.voznipark.maintenance.kafka.consumer;

import com.voznipark.maintenance.kafka.event.VehicleFaultReportedEvent;
import com.voznipark.maintenance.kafka.event.VehicleServiceScheduledEvent;
import com.voznipark.maintenance.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

// listens to fleet events that trigger maintenance obligations
@Slf4j
@Component
@RequiredArgsConstructor
public class FleetEventMaintenanceConsumer {

    private final MaintenanceService maintenanceService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "fleet.vehicle-service-scheduled",
            groupId = "technical-maintenance-service"
    )
    public void handleVehicleServiceScheduled(String message) {
        try {
            VehicleServiceScheduledEvent event =
                    objectMapper.readValue(message, VehicleServiceScheduledEvent.class);
            log.info("Received VehicleServiceScheduled | vehicleId={} serviceType={}",
                    event.getVehicleId(), event.getServiceType());
            maintenanceService.createServiceRecordFromScheduled(event);
        } catch (Exception e) {
            log.error("Failed to process VehicleServiceScheduled | error={} payload={}",
                    e.getMessage(), message);
        }
    }

    // only vehicleInoperable=true faults create a ServiceRecord — non-critical faults are discarded
    @KafkaListener(
            topics = "fleet.vehicle-fault-reported",
            groupId = "technical-maintenance-service"
    )
    public void handleVehicleFaultReported(String message) {
        try {
            VehicleFaultReportedEvent event =
                    objectMapper.readValue(message, VehicleFaultReportedEvent.class);
            log.info("Received VehicleFaultReported | vehicleId={} inoperable={}",
                    event.getVehicleId(), event.getVehicleInoperable());

            if (Boolean.TRUE.equals(event.getVehicleInoperable())) {
                maintenanceService.createServiceRecordFromFault(event);
            } else {
                log.debug("VehicleFaultReported skipped — vehicle not inoperable | vehicleId={}",
                        event.getVehicleId());
            }
        } catch (Exception e) {
            log.error("Failed to process VehicleFaultReported | error={} payload={}",
                    e.getMessage(), message);
        }
    }
}
