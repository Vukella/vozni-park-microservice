package com.voznipark.fleet.controller;

import com.voznipark.fleet.dto.request.VehicleAssignedDriverRequestDTO;
import com.voznipark.fleet.dto.response.VehicleAssignedDriverLogResponseDTO;
import com.voznipark.fleet.dto.response.VehicleAssignedDriverResponseDTO;
import com.voznipark.fleet.service.VehicleAssignedDriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class VehicleAssignedDriverController {

    private final VehicleAssignedDriverService vehicleAssignedDriverService;

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<VehicleAssignedDriverResponseDTO> getCurrentAssignment(
            @PathVariable Long vehicleId) {
        return vehicleAssignedDriverService.getCurrentAssignment(vehicleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/vehicle/{vehicleId}/history")
    public ResponseEntity<List<VehicleAssignedDriverLogResponseDTO>> getAssignmentHistory(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleAssignedDriverService.getAssignmentHistory(vehicleId));
    }

    @PostMapping
    public ResponseEntity<VehicleAssignedDriverResponseDTO> assignDriver(
            @Valid @RequestBody VehicleAssignedDriverRequestDTO dto) {
        return ResponseEntity.ok(vehicleAssignedDriverService.assignDriver(dto));
    }
}