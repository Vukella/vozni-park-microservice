package com.voznipark.reporting.repository;

import com.voznipark.reporting.dto.response.ReportRecordMetadataDTO;
import com.voznipark.reporting.entity.ReportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// list queries select metadata columns only — BLOB never loaded except by findById()
@Repository
public interface ReportRecordRepository extends JpaRepository<ReportRecord, Long> {

    @Query("SELECT new com.voznipark.reporting.dto.response.ReportRecordMetadataDTO(" +
           "r.id, r.reportType, r.sourceEntityId, r.sourceReference, " +
           "r.vehicleSapNumber, r.generatedAt, r.fileName, r.contentType, r.fileSize) " +
           "FROM ReportRecord r ORDER BY r.generatedAt DESC")
    List<ReportRecordMetadataDTO> findAllMetadata();

    @Query("SELECT new com.voznipark.reporting.dto.response.ReportRecordMetadataDTO(" +
           "r.id, r.reportType, r.sourceEntityId, r.sourceReference, " +
           "r.vehicleSapNumber, r.generatedAt, r.fileName, r.contentType, r.fileSize) " +
           "FROM ReportRecord r WHERE r.reportType = :reportType ORDER BY r.generatedAt DESC")
    List<ReportRecordMetadataDTO> findAllMetadataByReportType(@Param("reportType") String reportType);

    @Query("SELECT new com.voznipark.reporting.dto.response.ReportRecordMetadataDTO(" +
           "r.id, r.reportType, r.sourceEntityId, r.sourceReference, " +
           "r.vehicleSapNumber, r.generatedAt, r.fileName, r.contentType, r.fileSize) " +
           "FROM ReportRecord r WHERE r.sourceEntityId = :sourceEntityId ORDER BY r.generatedAt DESC")
    List<ReportRecordMetadataDTO> findAllMetadataBySourceEntityId(@Param("sourceEntityId") Long sourceEntityId);

    @Query("SELECT new com.voznipark.reporting.dto.response.ReportRecordMetadataDTO(" +
           "r.id, r.reportType, r.sourceEntityId, r.sourceReference, " +
           "r.vehicleSapNumber, r.generatedAt, r.fileName, r.contentType, r.fileSize) " +
           "FROM ReportRecord r WHERE r.id = :id")
    Optional<ReportRecordMetadataDTO> findMetadataById(@Param("id") Long id);

    boolean existsBySourceEntityIdAndReportType(Long sourceEntityId, String reportType);
}
