package com.voznipark.notification.kafka.consumer;

import com.voznipark.notification.kafka.event.DriverReservationFailedEvent;
import com.voznipark.notification.kafka.event.DriversReservedEvent;
import com.voznipark.notification.kafka.event.VehicleReservationFailedEvent;
import com.voznipark.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaEventNotificationConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(topics = "fleet.drivers.reserved")
    public void onDriversReserved(String message) {
        log.debug("Received DriversReservedEvent: {}", message);
        try {
            DriversReservedEvent event = objectMapper.readValue(message, DriversReservedEvent.class);

            String subject = "✅ Putni nalog potvrđen — #" + event.getTravelOrderId();
            String body = String.format(
                    "Putni nalog #%d je potvrđen.%n%n" +
                            "Vozilo ID:  %d%n" +
                            "Vozači ID:  %s%n" +
                            "Vreme:      %s",
                    event.getTravelOrderId(),
                    event.getVehicleId(),
                    event.getDriverIds(),
                    event.getOccurredAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — DriversReserved (Saga confirmed): travelOrderId={}",
                        event.getTravelOrderId());
            } else {
                log.warn("Notification delivery failed — DriversReserved: travelOrderId={}",
                        event.getTravelOrderId());
            }

        } catch (Exception e) {
            log.error("Failed to process DriversReservedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "fleet.vehicle.reservation-failed")
    public void onVehicleReservationFailed(String message) {
        log.debug("Received VehicleReservationFailedEvent: {}", message);
        try {
            VehicleReservationFailedEvent event = objectMapper.readValue(message, VehicleReservationFailedEvent.class);

            String subject = "❌ Putni nalog odbijen — #" + event.getTravelOrderId();
            String body = String.format(
                    "Putni nalog #%d je odbijen: vozilo nije raspoloživo.%n%n" +
                            "Vozilo ID:  %d%n" +
                            "Razlog:     %s%n" +
                            "Vreme:      %s",
                    event.getTravelOrderId(),
                    event.getVehicleId(),
                    event.getReason(),
                    event.getOccurredAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — VehicleReservationFailed: travelOrderId={}, vehicleId={}",
                        event.getTravelOrderId(), event.getVehicleId());
            } else {
                log.warn("Notification delivery failed — VehicleReservationFailed: travelOrderId={}, vehicleId={}",
                        event.getTravelOrderId(), event.getVehicleId());
            }

        } catch (Exception e) {
            log.error("Failed to process VehicleReservationFailedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "fleet.driver.reservation-failed")
    public void onDriverReservationFailed(String message) {
        log.debug("Received DriverReservationFailedEvent: {}", message);
        try {
            DriverReservationFailedEvent event = objectMapper.readValue(message, DriverReservationFailedEvent.class);

            String subject = "❌ Putni nalog odbijen — #" + event.getTravelOrderId();
            String body = String.format(
                    "Putni nalog #%d je odbijen: vozač nije raspoloživ. Vozilo je otpušteno.%n%n" +
                            "Vozilo ID:    %d (oslobođeno kompenzacijom)%n" +
                            "Vozači ID:    %s%n" +
                            "Razlog:       %s%n" +
                            "Vreme:        %s",
                    event.getTravelOrderId(),
                    event.getVehicleId(),
                    event.getRequestedDriverIds(),
                    event.getReason(),
                    event.getOccurredAt()
            );

            boolean sent = emailService.sendAdminNotification(subject, body);
            if (sent) {
                log.info("Notification sent — DriverReservationFailed: travelOrderId={}, vehicleId={}",
                        event.getTravelOrderId(), event.getVehicleId());
            } else {
                log.warn("Notification delivery failed — DriverReservationFailed: travelOrderId={}, vehicleId={}",
                        event.getTravelOrderId(), event.getVehicleId());
            }

        } catch (Exception e) {
            log.error("Failed to process DriverReservationFailedEvent: {}", e.getMessage(), e);
        }
    }
}