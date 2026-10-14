package com.voznipark.reporting.service.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Shared layout constants and drawing utilities for PDF generation.
 * PDFBox origin (0,0) is bottom-left; Y increases upward.
 * PDType1Font (Helvetica) uses WinAnsiEncoding — Cyrillic and other non-Latin
 * characters are replaced with '?' by sanitizeForPdf().
 */
public abstract class AbstractPdfGenerator {

    protected static final float MARGIN       = 50f;
    protected static final float PAGE_WIDTH   = PDRectangle.A4.getWidth();   // 595.28 pts
    protected static final float PAGE_HEIGHT  = PDRectangle.A4.getHeight();  // 841.89 pts
    protected static final float LINE_HEIGHT  = 18f;
    protected static final float LABEL_INDENT = MARGIN + 10f;
    protected static final float VALUE_OFFSET = 160f; // relative to LABEL_INDENT

    protected static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Layout primitives

    protected float writeTitle(PDPageContentStream cs, PDType1Font font,
                               String text, float y) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000f * 16f;
        cs.beginText();
        cs.setFont(font, 16);
        cs.newLineAtOffset((PAGE_WIDTH - textWidth) / 2f, y);
        cs.showText(text);
        cs.endText();
        return y - LINE_HEIGHT * 2f;
    }

    protected float writeSeparator(PDPageContentStream cs, float y) throws IOException {
        cs.setLineWidth(0.75f);
        cs.moveTo(MARGIN, y);
        cs.lineTo(PAGE_WIDTH - MARGIN, y);
        cs.stroke();
        return y - LINE_HEIGHT;
    }

    protected float writeSectionHeader(PDPageContentStream cs, PDType1Font font,
                                       String text, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, 11);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(text);
        cs.endText();
        return y - LINE_HEIGHT;
    }

    // renders a label-value pair; value sanitized for WinAnsiEncoding
    protected float writeField(PDPageContentStream cs,
                               PDType1Font labelFont, PDType1Font valueFont,
                               String label, String value,
                               float y) throws IOException {
        String safeValue = truncateAndSanitize(value, 70);
        cs.beginText();
        cs.setFont(labelFont, 10);
        cs.newLineAtOffset(LABEL_INDENT, y);
        cs.showText(label);
        cs.setFont(valueFont, 10);
        cs.newLineAtOffset(VALUE_OFFSET, 0);
        cs.showText(safeValue);
        cs.endText();
        return y - LINE_HEIGHT;
    }

    protected void writeFooter(PDPageContentStream cs, PDType1Font font,
                               String text) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000f * 8f;
        cs.beginText();
        cs.setFont(font, 8);
        cs.newLineAtOffset((PAGE_WIDTH - textWidth) / 2f, MARGIN / 2f);
        cs.showText(text);
        cs.endText();
    }

    // Null-safe formatters

    protected String safeStr(Object value) {
        return value != null ? String.valueOf(value) : "N/A";
    }

    protected String safeDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }

    protected String safeBoolean(Boolean value) {
        if (value == null) return "N/A";
        return value ? "Yes" : "No";
    }

    protected String safeBigDecimal(BigDecimal value) {
        return value != null ? value.toPlainString() + " RSD" : "N/A";
    }

    // Encoding safety

    // replaces codepoint > 255 with '?' to prevent PDFBox IllegalArgumentException on Cyrillic
    protected String truncateAndSanitize(String value, int maxChars) {
        if (value == null) return "N/A";
        String sanitized = sanitizeForPdf(value);
        return sanitized.length() <= maxChars
                ? sanitized
                : sanitized.substring(0, maxChars - 1) + "\u2026"; // ellipsis falls inside 255
    }

    private String sanitizeForPdf(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            sb.append(c < 256 ? c : '?');
        }
        return sb.toString();
    }
}
