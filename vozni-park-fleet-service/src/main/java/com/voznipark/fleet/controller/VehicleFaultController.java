package com.voznipark.fleet.controller;

import com.voznipark.fleet.dto.request.VehicleFaultRequestDTO;
import com.voznipark.fleet.dto.response.VehicleFaultResponseDTO;
import com.voznipark.fleet.service.VehicleFaultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/faults")
@RequiredArgsConstructor
public class VehicleFaultController {

    private final VehicleFaultService vehicleFaultService;

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleFaultResponseDTO>> getFaultsByVehicle(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleFaultService.getFaultsByVehicle(vehicleId));
    }

    @GetMapping("/{faultId}")
    public ResponseEntity<VehicleFaultResponseDTO> getFaultById(@PathVariable Long faultId) {
        return ResponseEntity.ok(vehicleFaultService.getFaultById(faultId));
    }

    @PostMapping
    public ResponseEntity<VehicleFaultResponseDTO> reportFault(
            @Valid @RequestBody VehicleFaultRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleFaultService.reportFault(dto));
    }

    @PatchMapping("/{faultId}/resolve")
    public ResponseEntity<VehicleFaultResponseDTO> resolveFault(
            @PathVariable Long faultId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate resolutionDate) {
        return ResponseEntity.ok(vehicleFaultService.resolveFault(faultId, resolutionDate));
    }
}