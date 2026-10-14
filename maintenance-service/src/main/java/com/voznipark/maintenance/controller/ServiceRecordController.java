package com.voznipark.maintenance.controller;

import com.voznipark.maintenance.dto.ServiceRecordResponseDTO;
import com.voznipark.maintenance.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-records")
@RequiredArgsConstructor
public class ServiceRecordController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    public ResponseEntity<List<ServiceRecordResponseDTO>> getAllServiceRecords() {
        return ResponseEntity.ok(maintenanceService.getAllServiceRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRecordResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.getServiceRecordById(id));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getByVehicle(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(maintenanceService.getServiceRecordsByVehicle(vehicleId));
    }

    /**
     * Filter by status: PENDING | IN_PROGRESS | COMPLETED | CANCELLED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(maintenanceService.getServiceRecordsByStatus(status));
    }

    /**
     * Filter by triggerType: SCHEDULED | FAULT_REPAIR | MILEAGE
     */
    @GetMapping("/trigger/{triggerType}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getByTriggerType(
            @PathVariable String triggerType) {
        return ResponseEntity.ok(maintenanceService.getServiceRecordsByTriggerType(triggerType));
    }

    /**
     * Update status for a service record.
     * Body: { "status": "IN_PROGRESS" }
     * Valid values: PENDING | IN_PROGRESS | COMPLETED | CANCELLED
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceRecordResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        return ResponseEntity.ok(maintenanceService.updateServiceRecordStatus(id, newStatus));
    }
}
