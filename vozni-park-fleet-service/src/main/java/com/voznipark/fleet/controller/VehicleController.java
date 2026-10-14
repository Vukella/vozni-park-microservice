package com.voznipark.fleet.controller;

import com.voznipark.fleet.dto.request.VehicleRequestDTO;
import com.voznipark.fleet.dto.response.VehicleResponseDTO;
import com.voznipark.fleet.exception.EntityNotFoundException;
import com.voznipark.fleet.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        VehicleResponseDTO vehicle = vehicleService.getVehicleById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO dto) {
        VehicleResponseDTO created = vehicleService.createVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO dto) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, dto));
    }

    /**
     * Permanent decommission — distinct from delete. The vehicle row is
     * never removed; decommissioned=true excludes it from all operational
     * queries and status recalculation going forward.
     */
    @PatchMapping("/{id}/decommission")
    public ResponseEntity<Void> decommissionVehicle(@PathVariable Long id) {
        vehicleService.decommissionVehicle(id);
        return ResponseEntity.noContent().build();
    }
}