package com.voznipark.fueltracking.service;

import com.voznipark.fueltracking.dto.response.FuelRecordDTO;
import com.voznipark.fueltracking.dto.response.FuelSummaryDTO;
import com.voznipark.fueltracking.entity.FuelRecord;
import com.voznipark.fueltracking.exception.EntityNotFoundException;
import com.voznipark.fueltracking.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.fueltracking.repository.FuelRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuelRecordService {

    private final FuelRecordRepository repository;

    @Value("${fuel.price.per.liter}")
    private BigDecimal pricePerLiter;

    @Value("${fuel.consumption.per.100km}")
    private BigDecimal consumptionPer100km;

    @Transactional
    public void processCompletedTravelOrder(TravelOrderCompletedEvent event) {
        if (repository.existsByTravelOrderId(event.getTravelOrderId())) {
            log.info("FuelRecord for travelOrderId={} already exists, skipping (likely Kafka replay)",
                    event.getTravelOrderId());
            return;
        }

        Integer distanceKm = event.getDistanceKm();
        if (distanceKm == null || distanceKm <= 0) {
            log.warn("TravelOrderCompletedEvent for order {} has distanceKm={} — skipping fuel calculation",
                    event.getTravelOrderId(), distanceKm);
            return;
        }

        BigDecimal distance        = BigDecimal.valueOf(distanceKm);
        BigDecimal fuelConsumed    = distance.multiply(consumptionPer100km)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalCost       = fuelConsumed.multiply(pricePerLiter)
                .setScale(2, RoundingMode.HALF_UP);

        FuelRecord record = FuelRecord.builder()
                .travelOrderId(event.getTravelOrderId())
                .travelOrderNumber(event.getTravelOrderNumber())
                .vehicleId(event.getVehicleId())
                .vehicleSapNumber(event.getVehicleSapNumber())
                .distanceKm(distanceKm)
                .fuelConsumedLiters(fuelConsumed)
                .pricePerLiter(pricePerLiter)
                .totalFuelCost(totalCost)
                .recordedAt(Instant.now())
                .build();

        repository.save(record);
        log.info("FuelRecord created for travelOrderId={}: {}L consumed, {} RSD total",
                event.getTravelOrderId(), fuelConsumed, totalCost);
    }

    @Transactional(readOnly = true)
    public List<FuelRecordDTO> findAll() {
        return repository.findAllByOrderByRecordedAtDesc().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuelRecordDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("FuelRecord not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<FuelRecordDTO> findByVehicleId(Long vehicleId) {
        return repository.findByVehicleId(vehicleId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuelSummaryDTO getSummary() {
        FuelSummaryDTO summary = repository.getSummary();
        // SUM returns null when the table is empty
        if (summary.getTotalDistanceKm() == null) {
            return new FuelSummaryDTO(0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return summary;
    }

    private FuelRecordDTO toDTO(FuelRecord r) {
        return FuelRecordDTO.builder()
                .id(r.getId())
                .travelOrderId(r.getTravelOrderId())
                .travelOrderNumber(r.getTravelOrderNumber())
                .vehicleId(r.getVehicleId())
                .vehicleSapNumber(r.getVehicleSapNumber())
                .distanceKm(r.getDistanceKm())
                .fuelConsumedLiters(r.getFuelConsumedLiters())
                .pricePerLiter(r.getPricePerLiter())
                .totalFuelCost(r.getTotalFuelCost())
                .recordedAt(r.getRecordedAt())
                .build();
    }
}