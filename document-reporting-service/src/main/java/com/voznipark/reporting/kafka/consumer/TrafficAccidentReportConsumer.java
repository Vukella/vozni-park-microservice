package com.voznipark.reporting.kafka.consumer;

import com.voznipark.reporting.kafka.event.TrafficAccidentReportedEvent;
import com.voznipark.reporting.service.ReportGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrafficAccidentReportConsumer {

    private final ObjectMapper objectMapper;
    private final ReportGenerationService reportGenerationService;

    // REST call to fleet-service happens inside the service, not here
    @KafkaListener(topics = "fleet.traffic-accident-reported", groupId = "document-reporting-service")
    public void handleTrafficAccidentReported(String message) {
        try {
            TrafficAccidentReportedEvent event =
                    objectMapper.readValue(message, TrafficAccidentReportedEvent.class);
            log.info("TrafficAccidentReportedEvent received — accident {}, fetching detail and generating report",
                    event.getAccidentId());
            reportGenerationService.generateTrafficAccidentReport(event);
        } catch (Exception e) {
            log.error("Failed to process TrafficAccidentReportedEvent: {} | message: {}",
                    e.getMessage(), message, e);
        }
    }
}
