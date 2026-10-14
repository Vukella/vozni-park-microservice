package com.voznipark.maintenance.controller;

import com.voznipark.maintenance.dto.VehicleMileageTrackerResponseDTO;
import com.voznipark.maintenance.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mileage")
@RequiredArgsConstructor
public class VehicleMileageController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    public ResponseEntity<List<VehicleMileageTrackerResponseDTO>> getAllTrackers() {
        return ResponseEntity.ok(maintenanceService.getAllMileageTrackers());
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<VehicleMileageTrackerResponseDTO> getByVehicle(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(maintenanceService.getMileageTrackerByVehicle(vehicleId));
    }
}
