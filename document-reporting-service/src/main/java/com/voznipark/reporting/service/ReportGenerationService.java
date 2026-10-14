package com.voznipark.reporting.service;

import com.voznipark.reporting.client.FleetServiceClient;
import com.voznipark.reporting.dto.external.TrafficAccidentDetailDTO;
import com.voznipark.reporting.entity.ReportRecord;
import com.voznipark.reporting.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.reporting.kafka.event.TrafficAccidentReportedEvent;
import com.voznipark.reporting.repository.ReportRecordRepository;
import com.voznipark.reporting.service.pdf.TravelOrderPdfGenerator;
import com.voznipark.reporting.service.pdf.TrafficAccidentPdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationService {

    public static final String TYPE_TRAVEL_ORDER  = "TRAVEL_ORDER_COMPLETION";
    public static final String TYPE_TRAFFIC_ACCIDENT = "TRAFFIC_ACCIDENT";

    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                    .withZone(ZoneId.systemDefault());

    private final ReportRecordRepository       reportRecordRepository;
    private final FleetServiceClient           fleetServiceClient;
    private final TravelOrderPdfGenerator      travelOrderPdfGenerator;
    private final TrafficAccidentPdfGenerator  trafficAccidentPdfGenerator;

    // Travel order report

    @Transactional
    public void generateTravelOrderReport(TravelOrderCompletedEvent event) {
        if (reportRecordRepository.existsBySourceEntityIdAndReportType(
                event.getTravelOrderId(), TYPE_TRAVEL_ORDER)) {
            log.warn("Report already exists for travel order {} — skipping (likely Kafka replay)",
                    event.getTravelOrderId());
            return;
        }

        byte[] pdfBytes;
        try {
            pdfBytes = travelOrderPdfGenerator.generate(event);
        } catch (IOException e) {
            log.error("PDF generation failed for travel order {}: {}", event.getTravelOrderId(), e.getMessage(), e);
            return; // do not save a broken record; consumer logs and moves on
        }

        String fileName = String.format("travel-order-%s-%s.pdf",
                event.getTravelOrderNumber() != null
                        ? event.getTravelOrderNumber()
                        : event.getTravelOrderId(),
                FILE_DATE_FORMATTER.format(Instant.now()));

        ReportRecord record = ReportRecord.builder()
                .reportType(TYPE_TRAVEL_ORDER)
                .sourceEntityId(event.getTravelOrderId())
                .sourceReference(event.getTravelOrderNumber())
                .vehicleSapNumber(event.getVehicleSapNumber())
                .fileName(fileName)
                .contentType("application/pdf")
                .fileSize((long) pdfBytes.length)
                .reportContent(pdfBytes)
                .build();

        reportRecordRepository.save(record);
        log.info("Travel order completion report saved: {} ({} bytes)", fileName, pdfBytes.length);
    }

    // Traffic accident report

    @Transactional
    public void generateTrafficAccidentReport(TrafficAccidentReportedEvent event) {
        if (reportRecordRepository.existsBySourceEntityIdAndReportType(
                event.getAccidentId(), TYPE_TRAFFIC_ACCIDENT)) {
            log.warn("Report already exists for accident {} — skipping (likely Kafka replay)",
                    event.getAccidentId());
            return;
        }

        // Fetch full detail from fleet-service — this is why the REST call is here,
        // not in the consumer: service layer owns external dependencies.
        Optional<TrafficAccidentDetailDTO> detailOpt =
                fleetServiceClient.getAccidentById(event.getAccidentId());

        if (detailOpt.isEmpty()) {
            // fleet-service unavailable or returned 4xx/5xx.
            // Log and return — the Kafka message is committed so it won't retry.
            // In production, a retry queue (DLQ) would handle this case.
            log.error("Cannot generate accident report — fleet-service did not return accident {}. " +
                      "Report will not be created for this event.", event.getAccidentId());
            return;
        }

        TrafficAccidentDetailDTO detail = detailOpt.get();

        byte[] pdfBytes;
        try {
            pdfBytes = trafficAccidentPdfGenerator.generate(detail);
        } catch (IOException e) {
            log.error("PDF generation failed for accident {}: {}", event.getAccidentId(), e.getMessage(), e);
            return;
        }

        String fileName = String.format("accident-%d-%s.pdf",
                event.getAccidentId(),
                FILE_DATE_FORMATTER.format(Instant.now()));

        ReportRecord record = ReportRecord.builder()
                .reportType(TYPE_TRAFFIC_ACCIDENT)
                .sourceEntityId(event.getAccidentId())
                .sourceReference("ACCIDENT-" + event.getAccidentId())
                .vehicleSapNumber(event.getVehicleSapNumber())
                .fileName(fileName)
                .contentType("application/pdf")
                .fileSize((long) pdfBytes.length)
                .reportContent(pdfBytes)
                .build();

        reportRecordRepository.save(record);
        log.info("Traffic accident report saved: {} ({} bytes)", fileName, pdfBytes.length);
    }
}
