package com.voznipark.fleet.controller;

import com.voznipark.fleet.kafka.event.*;
import com.voznipark.fleet.kafka.producer.FleetEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;

/** Temporary test controller — verifies end-to-end Kafka publishing. */
@RestController
@RequestMapping("/api/test/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final FleetEventProducer producer;

    @PostMapping("/vehicle-status-changed")
    public ResponseEntity<String> vehicleStatusChanged() {
        producer.publishVehicleStatusChanged(VehicleStatusChangedEvent.builder()
                .vehicleId(1L)
                .sapNumber(10001)
                .oldStatus("AVAILABLE")
                .newStatus("ON_DUTY")
                .locationId(3L)
                .occurredAt(Instant.now())
                .build());
        return ResponseEntity.ok("VehicleStatusChanged published — check http://localhost:8090");
    }

    @PostMapping("/driver-status-changed")
    public ResponseEntity<String> driverStatusChanged() {
        producer.publishDriverStatusChanged(DriverStatusChangedEvent.builder()
                .driverId(1L)
                .sapNumber(20001)
                .fullName("Petar Petrović")
                .oldStatus("AVAILABLE")
                .newStatus("ON_DUTY")
                .locationId(3L)
                .occurredAt(Instant.now())
                .build());
        return ResponseEntity.ok("DriverStatusChanged published — check http://localhost:8090");
    }

    @PostMapping("/vehicle-fault-reported")
    public ResponseEntity<String> vehicleFaultReported() {
        producer.publishVehicleFaultReported(VehicleFaultReportedEvent.builder()
                .faultId(1L)
                .vehicleId(1L)
                .vehicleSapNumber(10001)
                .description("Motor oil leak detected during morning inspection")
                .vehicleInoperable(true)
                .locationId(3L)
                .occurredAt(Instant.now())
                .build());
        return ResponseEntity.ok("VehicleFaultReported published — check http://localhost:8090");
    }

    @PostMapping("/driver-license-revoked")
    public ResponseEntity<String> driverLicenseRevoked() {
        producer.publishDriverLicenseRevoked(DriverLicenseRevokedEvent.builder()
                .driverId(1L)
                .driverSapNumber(20001)
                .fullName("Petar Petrović")
                .revocationId(1L)
                .locationId(3L)
                .revokedAt(Instant.now())
                .build());
        return ResponseEntity.ok("DriverLicenseRevoked published — check http://localhost:8090");
    }

    @PostMapping("/document-expiring")
    public ResponseEntity<String> documentExpiring() {
        producer.publishDocumentExpiring(DocumentExpiringEvent.builder()
                .entityType("VEHICLE")
                .entityId(1L)
                .documentType("REGISTRATION")
                .expiryDate(LocalDate.now().plusDays(14))
                .daysUntilExpiry(14)
                .locationId(3L)
                .occurredAt(Instant.now())
                .build());
        return ResponseEntity.ok("DocumentExpiring published — check http://localhost:8090");
    }

    @PostMapping("/traffic-accident-reported")
    public ResponseEntity<String> trafficAccidentReported() {
        producer.publishTrafficAccidentReported(TrafficAccidentReportedEvent.builder()
                .accidentId(1L)
                .vehicleId(1L)
                .vehicleSapNumber(10001)
                .driverId(1L)
                .travelOrderNumberSnapshot("LOC3-2026-0042")
                .driverLicenseNumberSnapshot("B-123456")
                .locationId(3L)
                .reportedAt(Instant.now())
                .build());
        return ResponseEntity.ok("TrafficAccidentReported published — check http://localhost:8090");
    }
}
