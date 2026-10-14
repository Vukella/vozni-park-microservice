package com.voznipark.reporting.service.pdf;

import com.voznipark.reporting.dto.external.TrafficAccidentDetailDTO;
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

// all fields available — detail was fetched from fleet-service before this is called
@Component
@Slf4j
public class TrafficAccidentPdfGenerator extends AbstractPdfGenerator {

    private static final DateTimeFormatter TS_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public byte[] generate(TrafficAccidentDetailDTO accident) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font fontBold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                // Header
                y = writeTitle(cs, fontBold, "TRAFFIC ACCIDENT REPORT", y);
                y = writeSeparator(cs, y);
                y -= 5f;
                y = writeField(cs, fontBold, fontRegular,
                        "Generated at:", TS_FORMATTER.format(Instant.now()), y);
                y -= LINE_HEIGHT;

                // Accident identification
                y = writeSectionHeader(cs, fontBold, "ACCIDENT IDENTIFICATION", y);
                y -= 5f;
                y = writeField(cs, fontBold, fontRegular, "Accident ID:",
                        safeStr(accident.idTrafficAccident()), y);
                y = writeField(cs, fontBold, fontRegular, "Accident Date:",
                        safeDate(accident.accidentDate()), y);
                y = writeField(cs, fontBold, fontRegular, "Accident Time:",
                        safeStr(accident.accidentTime()), y);
                y = writeField(cs, fontBold, fontRegular, "Travel Order No.:",
                        safeStr(accident.travelOrderNumberSnapshot()), y);
                y -= LINE_HEIGHT;

                // Vehicle
                y = writeSectionHeader(cs, fontBold, "VEHICLE INFORMATION", y);
                y -= 5f;
                if (accident.vehicle() != null) {
                    y = writeField(cs, fontBold, fontRegular, "SAP Number:",
                            safeStr(accident.vehicle().sapNumber()), y);
                    y = writeField(cs, fontBold, fontRegular, "Chassis Number:",
                            safeStr(accident.vehicle().chassisNumber()), y);
                } else {
                    y = writeField(cs, fontBold, fontRegular, "Vehicle:", "N/A", y);
                }
                y -= LINE_HEIGHT;

                // Driver
                y = writeSectionHeader(cs, fontBold, "DRIVER INFORMATION", y);
                y -= 5f;
                if (accident.driver() != null) {
                    y = writeField(cs, fontBold, fontRegular, "Full Name:",
                            safeStr(accident.driver().fullName()), y);
                    y = writeField(cs, fontBold, fontRegular, "Driver SAP No.:",
                            safeStr(accident.driver().sapNumber()), y);
                }
                y = writeField(cs, fontBold, fontRegular, "License Number:",
                        safeStr(accident.driverLicenseNumberSnapshot()), y);
                y = writeField(cs, fontBold, fontRegular, "License Expiry:",
                        safeDate(accident.driverLicenseExpirySnapshot()), y);
                y -= LINE_HEIGHT;

                // Organizational unit + physical location
                y = writeSectionHeader(cs, fontBold, "LOCATION", y);
                y -= 5f;
                if (accident.location() != null) {
                    y = writeField(cs, fontBold, fontRegular, "Org. Unit:",
                            safeStr(accident.location().locationName()), y);
                    y = writeField(cs, fontBold, fontRegular, "Unit Address:",
                            safeStr(accident.location().locationAddress()), y);
                }
                y = writeField(cs, fontBold, fontRegular, "Accident Location:",
                        safeStr(accident.locationDescription()), y);
                y -= LINE_HEIGHT;

                // Accident details
                y = writeSectionHeader(cs, fontBold, "ACCIDENT DETAILS", y);
                y -= 5f;
                y = writeField(cs, fontBold, fontRegular, "Description:",
                        safeStr(accident.accidentDescription()), y);
                y = writeField(cs, fontBold, fontRegular, "Damage:",
                        safeStr(accident.damageDescription()), y);
                y = writeField(cs, fontBold, fontRegular, "Police Reported:",
                        safeBoolean(accident.reportedToPolice()), y);
                if (Boolean.FALSE.equals(accident.reportedToPolice())) {
                    y = writeField(cs, fontBold, fontRegular, "Reason (not reported):",
                            safeStr(accident.notReportedReason()), y);
                }
                y = writeField(cs, fontBold, fontRegular, "Estimated Damage:",
                        safeBigDecimal(accident.estimatedDamageValue()), y);
                y = writeField(cs, fontBold, fontRegular, "Other Participants:",
                        safeStr(accident.otherParticipants()), y);
                y -= LINE_HEIGHT;

                // Declarant
                y = writeSectionHeader(cs, fontBold, "DECLARANT INFORMATION", y);
                y -= 5f;
                y = writeField(cs, fontBold, fontRegular, "National ID:",
                        safeStr(accident.declarantNationalId()), y);
                y = writeField(cs, fontBold, fontRegular, "Address:",
                        safeStr(accident.declarantAddress()), y);
                y = writeField(cs, fontBold, fontRegular, "Email:",
                        safeStr(accident.declarantEmail()), y);
                writeField(cs, fontBold, fontRegular, "Position:",
                        safeStr(accident.declarantPosition()), y);

                writeFooter(cs, fontRegular, "Vozni Park  —  Document Reporting Service");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            log.debug("Traffic accident PDF generated: {} bytes", out.size());
            return out.toByteArray();
        }
    }
}
