package com.voznipark.fueltracking.controller;

import com.voznipark.fueltracking.dto.response.FuelRecordDTO;
import com.voznipark.fueltracking.dto.response.FuelSummaryDTO;
import com.voznipark.fueltracking.service.FuelRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuel-records")
@RequiredArgsConstructor
public class FuelRecordController {

    private final FuelRecordService service;

    @GetMapping
    public ResponseEntity<List<FuelRecordDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuelRecordDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<FuelRecordDTO>> getByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(service.findByVehicleId(vehicleId));
    }

    @GetMapping("/summary")
    public ResponseEntity<FuelSummaryDTO> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }
}