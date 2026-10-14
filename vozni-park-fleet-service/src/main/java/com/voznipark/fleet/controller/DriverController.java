package com.voznipark.fleet.controller;

import com.voznipark.fleet.dto.request.DriverRequestDTO;
import com.voznipark.fleet.dto.response.DriverResponseDTO;
import com.voznipark.fleet.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DriverResponseDTO>> getDriversByStatus(@PathVariable String status) {
        return ResponseEntity.ok(driverService.getDriversByStatus(status));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<DriverResponseDTO>> getDriversByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(driverService.getDriversByLocation(locationId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DriverResponseDTO>> searchDriversByName(@RequestParam String name) {
        return ResponseEntity.ok(driverService.searchDriversByName(name));
    }

    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverRequestDTO dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}