package com.voznipark.fleet.controller;

import com.voznipark.fleet.dto.request.TrafficAccidentRequestDTO;
import com.voznipark.fleet.dto.response.TrafficAccidentResponseDTO;
import com.voznipark.fleet.service.TrafficAccidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traffic-accidents")
@RequiredArgsConstructor
public class TrafficAccidentController {

    private final TrafficAccidentService trafficAccidentService;

    @GetMapping("/{id}")
    public ResponseEntity<TrafficAccidentResponseDTO> getAccidentById(@PathVariable Long id) {
        return ResponseEntity.ok(trafficAccidentService.getAccidentById(id));
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<TrafficAccidentResponseDTO>> getAccidentsByVehicle(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(trafficAccidentService.getAccidentsByVehicle(vehicleId));
    }

    @GetMapping("/by-driver/{driverId}")
    public ResponseEntity<List<TrafficAccidentResponseDTO>> getAccidentsByDriver(
            @PathVariable Long driverId) {
        return ResponseEntity.ok(trafficAccidentService.getAccidentsByDriver(driverId));
    }

    @PostMapping
    public ResponseEntity<TrafficAccidentResponseDTO> reportAccident(
            @Valid @RequestBody TrafficAccidentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trafficAccidentService.reportAccident(dto));
    }
}