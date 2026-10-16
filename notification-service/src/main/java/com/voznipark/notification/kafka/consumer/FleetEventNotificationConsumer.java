package com.voznipark.notification.kafka.consumer;

import com.voznipark.notification.kafka.event.*;
import com.voznipark.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class FleetEventNotificationConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(topics = "fleet.vehicle-fault-reported")
    public void onVehicleFaultReported(String message) {
        log.debug("Received VehicleFaultReportedEvent: {}", message);
        try {
            VehicleFaultReportedEvent event = objectMapper.readValue(message, VehicleFaultReportedEvent.class);

            String subject = "🔧 Kvar vozila — SAP " + event.getVehicleSapNumber();
            String body = String.format(
                    "Vozilo SAP %d je prijavljeno kao neispravno.%n%n" +
                            "ID kvara:       %d%n" +
                            "Opis:           %s%n" +
                            "Vozilo u kvaru: %s%n" +
                            "Vreme:          %s",
                    event.getVehicleSapNumber(),
                    event.getFaultId(),
                    event.getDescription(),
                    Boolean.TRUE.equals(event.getVehicleInoperable()) ? "Da" : "Ne",
                    event.getOccurredAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — VehicleFaultReported: vehicleId={}, faultId={}",
                        event.getVehicleId(), event.getFaultId());
            } else {
                log.warn("Notification delivery failed — VehicleFaultReported: vehicleId={}, faultId={}",
                        event.getVehicleId(), event.getFaultId());
            }

        } catch (Exception e) {
            log.error("Failed to process VehicleFaultReportedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "fleet.vehicle-fault-resolved")
    public void onVehicleFaultResolved(String message) {
        log.debug("Received VehicleFaultResolvedEvent: {}", message);
        try {
            VehicleFaultResolvedEvent event = objectMapper.readValue(message, VehicleFaultResolvedEvent.class);

            String subject = "✅ Kvar rešen — SAP " + event.getVehicleSapNumber();
            String body = String.format(
                    "Kvar na vozilu SAP %d je rešen.%n%n" +
                            "ID kvara:        %d%n" +
                            "Datum rešavanja: %s%n" +
                            "Vreme eventa:    %s",
                    event.getVehicleSapNumber(),
                    event.getFaultId(),
                    event.getResolvedAt(),
                    event.getResolvedAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — VehicleFaultResolved: vehicleId={}, faultId={}",
                        event.getVehicleId(), event.getFaultId());
            } else {
                log.warn("Notification delivery failed — VehicleFaultResolved: vehicleId={}, faultId={}",
                        event.getVehicleId(), event.getFaultId());
            }

        } catch (Exception e) {
            log.error("Failed to process VehicleFaultResolvedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "fleet.document-expiring")
    public void onDocumentExpiring(String message) {
        log.debug("Received DocumentExpiringEvent: {}", message);
        try {
            DocumentExpiringEvent event = objectMapper.readValue(message, DocumentExpiringEvent.class);

            String subject = String.format("⚠️ Dokument ističe — %s (%s %d)",
                    event.getDocumentType(), event.getEntityType(), event.getEntityId());
            String body = String.format(
                    "%s %d: dokument %s ističe za %d %s.%n%n" +
                            "Datum isteka: %s%n" +
                            "Vreme:        %s",
                    event.getEntityType(),
                    event.getEntityId(),
                    event.getDocumentType(),
                    event.getDaysUntilExpiry(),
                    event.getDaysUntilExpiry() == 1 ? "dan" : "dana",
                    event.getExpiryDate(),
                    event.getOccurredAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — DocumentExpiring: entityType={}, entityId={}, documentType={}, daysUntilExpiry={}",
                        event.getEntityType(), event.getEntityId(),
                        event.getDocumentType(), event.getDaysUntilExpiry());
            } else {
                log.warn("Notification delivery failed — DocumentExpiring: entityType={}, entityId={}, documentType={}",
                        event.getEntityType(), event.getEntityId(), event.getDocumentType());
            }

        } catch (Exception e) {
            log.error("Failed to process DocumentExpiringEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "fleet.driver-license-revoked")
    public void onDriverLicenseRevoked(String message) {
        log.debug("Received DriverLicenseRevokedEvent: {}", message);
        try {
            DriverLicenseRevokedEvent event = objectMapper.readValue(message, DriverLicenseRevokedEvent.class);

            String subject = "🚫 Vozačka dozvola oduzeta — vozač ID " + event.getDriverId();
            String body = String.format(
                    "Vozaču %d je oduzeta vozačka dozvola.%n%n" +
                            "Status promenjen u: SUSPENDED%n" +
                            "Vreme:              %s",
                    event.getDriverId(),
                    event.getRevokedAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — DriverLicenseRevoked: driverId={}", event.getDriverId());
            } else {
                log.warn("Notification delivery failed — DriverLicenseRevoked: driverId={}", event.getDriverId());
            }

        } catch (Exception e) {
            log.error("Failed to process DriverLicenseRevokedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "fleet.driver-license-restored")
    public void onDriverLicenseRestored(String message) {
        log.debug("Received DriverLicenseRestoredEvent: {}", message);
        try {
            DriverLicenseRestoredEvent event = objectMapper.readValue(message, DriverLicenseRestoredEvent.class);

            String subject = "✅ Vozačka dozvola vraćena — vozač ID " + event.getDriverId();
            String body = String.format(
                    "Vozaču %d je vraćena vozačka dozvola.%n%n" +
                            "Status promenjen u: AVAILABLE%n" +
                            "Vreme:              %s",
                    event.getDriverId(),
                    event.getRestoredAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — DriverLicenseRestored: driverId={}", event.getDriverId());
            } else {
                log.warn("Notification delivery failed — DriverLicenseRestored: driverId={}", event.getDriverId());
            }

        } catch (Exception e) {
            log.error("Failed to process DriverLicenseRestoredEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "fleet.traffic-accident-reported")
    public void onTrafficAccidentReported(String message) {
        log.debug("Received TrafficAccidentReportedEvent: {}", message);
        try {
            TrafficAccidentReportedEvent event = objectMapper.readValue(message, TrafficAccidentReportedEvent.class);

            String subject = "🚨 Saobraćajna nesreća — vozilo SAP " + event.getVehicleSapNumber();
            String body = String.format(
                    "Prijavljena saobraćajna nesreća.%n%n" +
                            "ID nesreće:         %d%n" +
                            "Vozilo ID:          %d (SAP %d)%n" +
                            "Vozač ID:           %d%n" +
                            "Broj putnog naloga: %s%n" +
                            "Br. vozačke dozvole:%s%n" +
                            "Vreme:              %s",
                    event.getAccidentId(),
                    event.getVehicleId(),
                    event.getVehicleSapNumber(),
                    event.getDriverId(),
                    event.getTravelOrderNumberSnapshot(),
                    event.getDriverLicenseNumberSnapshot(),
                    event.getReportedAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — TrafficAccidentReported: accidentId={}, vehicleId={}, driverId={}",
                        event.getAccidentId(), event.getVehicleId(), event.getDriverId());
            } else {
                log.warn("Notification delivery failed — TrafficAccidentReported: accidentId={}, vehicleId={}",
                        event.getAccidentId(), event.getVehicleId());
            }

        } catch (Exception e) {
            log.error("Failed to process TrafficAccidentReportedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "fleet.vehicle-decommissioned")
    public void onVehicleDecommissioned(String message) {
        log.debug("Received VehicleDecommissionedEvent: {}", message);
        try {
            VehicleDecommissionedEvent event = objectMapper.readValue(message, VehicleDecommissionedEvent.class);

            String subject = "📋 Vozilo rashodovano — SAP " + event.getVehicleSapNumber();
            String body = String.format(
                    "Vozilo SAP %d je rashodovano.%n%n" +
                            "Vozilo ID:          %d%n" +
                            "Datum rashodovanja: %s%n" +
                            "Vreme eventa:       %s",
                    event.getVehicleSapNumber(),
                    event.getVehicleId(),
                    event.getDecommissionDate(),
                    event.getOccurredAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — VehicleDecommissioned: vehicleId={}, SAP={}",
                        event.getVehicleId(), event.getVehicleSapNumber());
            } else {
                log.warn("Notification delivery failed — VehicleDecommissioned: vehicleId={}, SAP={}",
                        event.getVehicleId(), event.getVehicleSapNumber());
            }

        } catch (Exception e) {
            log.error("Failed to process VehicleDecommissionedEvent: {}", e.getMessage(), e);
        }
    }
}