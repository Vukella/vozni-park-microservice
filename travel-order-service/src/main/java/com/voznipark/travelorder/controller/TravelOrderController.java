package com.voznipark.travelorder.controller;

import com.voznipark.travelorder.dto.request.CompleteOrderRequestDTO;
import com.voznipark.travelorder.dto.request.TravelOrderRequestDTO;
import com.voznipark.travelorder.dto.response.TravelOrderResponseDTO;
import com.voznipark.travelorder.service.TravelOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-orders")
@RequiredArgsConstructor
public class TravelOrderController {

    private final TravelOrderService travelOrderService;

    @GetMapping
    public ResponseEntity<List<TravelOrderResponseDTO>> getAllTravelOrders() {
        return ResponseEntity.ok(travelOrderService.getAllTravelOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelOrderResponseDTO> getTravelOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(travelOrderService.getTravelOrderById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TravelOrderResponseDTO>> getTravelOrdersByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(travelOrderService.getTravelOrdersByStatus(status));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<TravelOrderResponseDTO>> getTravelOrdersByVehicle(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(travelOrderService.getTravelOrdersByVehicle(vehicleId));
    }

    // returns 201 PENDING immediately — Saga runs async
    @PostMapping
    public ResponseEntity<TravelOrderResponseDTO> createTravelOrder(
            @RequestBody @Valid TravelOrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(travelOrderService.createTravelOrder(dto));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TravelOrderResponseDTO> completeTravelOrder(
            @PathVariable Long id,
            @RequestBody @Valid CompleteOrderRequestDTO dto) {
        return ResponseEntity.ok(travelOrderService.completeTravelOrder(id, dto));
    }
}