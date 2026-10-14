package com.voznipark.fleet.kafka.producer;

import com.voznipark.fleet.kafka.event.*;
import com.voznipark.fleet.kafka.topics.FleetTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FleetEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Vehicle events

    public void publishVehicleStatusChanged(VehicleStatusChangedEvent event) {
        send(FleetTopics.VEHICLE_STATUS_CHANGED, String.valueOf(event.getVehicleId()), event);
    }

    public void publishVehicleFaultReported(VehicleFaultReportedEvent event) {
        send(FleetTopics.VEHICLE_FAULT_REPORTED, String.valueOf(event.getVehicleId()), event);
    }

    public void publishVehicleFaultResolved(VehicleFaultResolvedEvent event) {
        send(FleetTopics.VEHICLE_FAULT_RESOLVED, String.valueOf(event.getVehicleId()), event);
    }

    public void publishVehicleServiceScheduled(VehicleServiceScheduledEvent event) {
        send(FleetTopics.VEHICLE_SERVICE_SCHEDULED, String.valueOf(event.getVehicleId()), event);
    }

    public void publishVehicleDecommissioned(VehicleDecommissionedEvent event) {
        send(FleetTopics.VEHICLE_DECOMMISSIONED, String.valueOf(event.getVehicleId()), event);
    }

    public void publishVehicleDocumentUploaded(VehicleDocumentUploadedEvent event) {
        send(FleetTopics.VEHICLE_DOCUMENT_UPLOADED, String.valueOf(event.getVehicleId()), event);
    }

    public void publishRegistrationNumberChanged(RegistrationNumberChangedEvent event) {
        send(FleetTopics.REGISTRATION_NUMBER_CHANGED, String.valueOf(event.getVehicleId()), event);
    }

    public void publishVehicleAssignedDriverChanged(VehicleAssignedDriverChangedEvent event) {
        send(FleetTopics.VEHICLE_ASSIGNED_DRIVER_CHANGED, String.valueOf(event.getVehicleId()), event);
    }

    // Driver events

    public void publishDriverStatusChanged(DriverStatusChangedEvent event) {
        send(FleetTopics.DRIVER_STATUS_CHANGED, String.valueOf(event.getDriverId()), event);
    }

    public void publishDriverLicenseRevoked(DriverLicenseRevokedEvent event) {
        send(FleetTopics.DRIVER_LICENSE_REVOKED, String.valueOf(event.getDriverId()), event);
    }

    public void publishDriverLicenseRestored(DriverLicenseRestoredEvent event) {
        send(FleetTopics.DRIVER_LICENSE_RESTORED, String.valueOf(event.getDriverId()), event);
    }

    public void publishTrafficAccidentReported(TrafficAccidentReportedEvent event) {
        send(FleetTopics.TRAFFIC_ACCIDENT_REPORTED, String.valueOf(event.getVehicleId()), event);
    }

    // Shared events

    public void publishDocumentExpiring(DocumentExpiringEvent event) {
        send(FleetTopics.DOCUMENT_EXPIRING, String.valueOf(event.getEntityId()), event);
    }

    // Saga events

    public void publishVehicleReserved(VehicleReservedEvent event) {
        send(FleetTopics.VEHICLE_RESERVED, String.valueOf(event.getTravelOrderId()), event);
    }

    public void publishVehicleReservationFailed(VehicleReservationFailedEvent event) {
        send(FleetTopics.VEHICLE_RESERVATION_FAILED, String.valueOf(event.getTravelOrderId()), event);
    }

    public void publishDriversReserved(DriversReservedEvent event) {
        send(FleetTopics.DRIVERS_RESERVED, String.valueOf(event.getTravelOrderId()), event);
    }

    public void publishDriverReservationFailed(DriverReservationFailedEvent event) {
        send(FleetTopics.DRIVER_RESERVATION_FAILED, String.valueOf(event.getTravelOrderId()), event);
    }

    public void publishVehicleReleased(VehicleReleasedEvent event) {
        send(FleetTopics.VEHICLE_RELEASED, String.valueOf(event.getTravelOrderId()), event);
    }

    // Internal

    private void send(String topic, String key, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(topic, key, json);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event | topic={} key={} error={}",
                            topic, key, ex.getMessage());
                } else {
                    log.debug("Event published | topic={} partition={} offset={}",
                            topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("Failed to serialize event | topic={} key={} error={}",
                    topic, key, e.getMessage());
        }
    }
}
