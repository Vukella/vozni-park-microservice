package com.voznipark.reporting.controller;

import com.voznipark.reporting.dto.response.ReportRecordMetadataDTO;
import com.voznipark.reporting.entity.ReportRecord;
import com.voznipark.reporting.exception.EntityNotFoundException;
import com.voznipark.reporting.repository.ReportRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// list endpoints return metadata only — BLOB never loaded for lists
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRecordRepository reportRecordRepository;

    @GetMapping
    public ResponseEntity<List<ReportRecordMetadataDTO>> getAllReports() {
        return ResponseEntity.ok(reportRecordRepository.findAllMetadata());
    }

    @GetMapping("/by-type/{reportType}")
    public ResponseEntity<List<ReportRecordMetadataDTO>> getByType(
            @PathVariable String reportType) {
        return ResponseEntity.ok(
                reportRecordRepository.findAllMetadataByReportType(reportType));
    }

    @GetMapping("/by-source/{sourceEntityId}")
    public ResponseEntity<List<ReportRecordMetadataDTO>> getBySource(
            @PathVariable Long sourceEntityId) {
        return ResponseEntity.ok(
                reportRecordRepository.findAllMetadataBySourceEntityId(sourceEntityId));
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<ReportRecordMetadataDTO> getMetadata(@PathVariable Long id) {
        return reportRecordRepository.findMetadataById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("ReportRecord", id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        ReportRecord report = reportRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ReportRecord", id));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + report.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH,
                        String.valueOf(report.getFileSize()))
                .body(report.getReportContent());
    }
}
