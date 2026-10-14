package com.voznipark.reporting.kafka.consumer;

import com.voznipark.reporting.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.reporting.service.ReportGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class TravelOrderReportConsumer {

    private final ObjectMapper objectMapper;
    private final ReportGenerationService reportGenerationService;

    @KafkaListener(topics = "travel-order.completed", groupId = "document-reporting-service")
    public void handleTravelOrderCompleted(String message) {
        try {
            TravelOrderCompletedEvent event =
                    objectMapper.readValue(message, TravelOrderCompletedEvent.class);
            log.info("TravelOrderCompletedEvent received — order {}, generating report",
                    event.getTravelOrderId());
            reportGenerationService.generateTravelOrderReport(event);
        } catch (Exception e) {
            log.error("Failed to process TravelOrderCompletedEvent: {} | message: {}",
                    e.getMessage(), message, e);
        }
    }
}
