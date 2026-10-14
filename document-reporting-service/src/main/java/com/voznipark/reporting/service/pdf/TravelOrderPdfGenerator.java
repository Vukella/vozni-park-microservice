package com.voznipark.reporting.service.pdf;

import com.voznipark.reporting.kafka.event.TravelOrderCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

// driverIds contains Long IDs only — full names would require a fleet-service call not worth the coupling
@Component
@Slf4j
public class TravelOrderPdfGenerator extends AbstractPdfGenerator {

    private static final DateTimeFormatter TS_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public byte[] generate(TravelOrderCompletedEvent event) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font fontBold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                // Header
                y = writeTitle(cs, fontBold, "TRAVEL ORDER COMPLETION REPORT", y);
                y = writeSeparator(cs, y);
                y -= 5f;
                y = writeField(cs, fontBold, fontRegular,
                        "Generated at:", TS_FORMATTER.format(Instant.now()), y);
                y -= LINE_HEIGHT;

                // Travel order details
                y = writeSectionHeader(cs, fontBold, "TRAVEL ORDER DETAILS", y);
                y -= 5f;
                y = writeField(cs, fontBold, fontRegular, "Travel Order ID:",
                        safeStr(event.getTravelOrderId()), y);
                y = writeField(cs, fontBold, fontRegular, "Travel Order Number:",
                        safeStr(event.getTravelOrderNumber()), y);
                y -= LINE_HEIGHT;

                // Vehicle
                y = writeSectionHeader(cs, fontBold, "VEHICLE INFORMATION", y);
                y -= 5f;
                y = writeField(cs, fontBold, fontRegular, "Vehicle ID:",
                        safeStr(event.getVehicleId()), y);
                y = writeField(cs, fontBold, fontRegular, "Vehicle SAP Number:",
                        safeStr(event.getVehicleSapNumber()), y);
                y -= LINE_HEIGHT;

                // Drivers
                y = writeSectionHeader(cs, fontBold, "ASSIGNED DRIVER(S)", y);
                y -= 5f;
                List<Long> driverIds = event.getDriverIds();
                if (driverIds != null && !driverIds.isEmpty()) {
                    for (int i = 0; i < driverIds.size(); i++) {
                        y = writeField(cs, fontBold, fontRegular,
                                "Driver ID " + (i + 1) + ":", String.valueOf(driverIds.get(i)), y);
                    }
                } else {
                    y = writeField(cs, fontBold, fontRegular, "Driver(s):", "N/A", y);
                }
                y -= LINE_HEIGHT;

                // Mileage
                y = writeSectionHeader(cs, fontBold, "MILEAGE SUMMARY", y);
                y -= 5f;
                y = writeField(cs, fontBold, fontRegular, "Starting Mileage (km):",
                        safeStr(event.getStartingMileage()), y);
                y = writeField(cs, fontBold, fontRegular, "Ending Mileage (km):",
                        safeStr(event.getEndingMileage()), y);
                y = writeField(cs, fontBold, fontRegular, "Distance Traveled (km):",
                        safeStr(event.getDistanceKm()), y);
                y -= LINE_HEIGHT;

                // Timestamp
                y = writeSectionHeader(cs, fontBold, "COMPLETION TIMESTAMP", y);
                y -= 5f;
                String completedAt = event.getOccurredAt() != null
                        ? TS_FORMATTER.format(event.getOccurredAt()) : "N/A";
                writeField(cs, fontBold, fontRegular, "Completed At:", completedAt, y);

                writeFooter(cs, fontRegular, "Vozni Park  —  Document Reporting Service");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            log.debug("Travel order PDF generated: {} bytes", out.size());
            return out.toByteArray();
        }
    }
}
