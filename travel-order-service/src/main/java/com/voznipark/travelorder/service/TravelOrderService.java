package com.voznipark.travelorder.service;

import com.voznipark.travelorder.dto.request.CompleteOrderRequestDTO;
import com.voznipark.travelorder.dto.request.TravelOrderRequestDTO;
import com.voznipark.travelorder.dto.response.TravelOrderResponseDTO;
import com.voznipark.travelorder.entity.TravelOrder;
import com.voznipark.travelorder.entity.TravelOrderDriver;
import com.voznipark.travelorder.exception.EntityNotFoundException;
import com.voznipark.travelorder.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.travelorder.kafka.event.TravelOrderRequestedEvent;
import com.voznipark.travelorder.kafka.producer.TravelOrderEventProducer;
import com.voznipark.travelorder.mapper.TravelOrderMapper;
import com.voznipark.travelorder.repository.TravelOrderDriverRepository;
import com.voznipark.travelorder.repository.TravelOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TravelOrderService {

    private final TravelOrderRepository travelOrderRepository;
    private final TravelOrderDriverRepository travelOrderDriverRepository;
    private final TravelOrderMapper travelOrderMapper;
    private final TravelOrderEventProducer eventProducer;

    // Queries

    public List<TravelOrderResponseDTO> getAllTravelOrders() {
        return travelOrderRepository.findAll().stream()
                .map(travelOrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public TravelOrderResponseDTO getTravelOrderById(Long id) {
        return travelOrderRepository.findByIdWithDrivers(id)
                .map(travelOrderMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("TravelOrder", id));
    }

    public List<TravelOrderResponseDTO> getTravelOrdersByStatus(String status) {
        return travelOrderRepository.findByStatus(status).stream()
                .map(travelOrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TravelOrderResponseDTO> getTravelOrdersByVehicle(Long vehicleId) {
        return travelOrderRepository.findByVehicleId(vehicleId).stream()
                .map(travelOrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Mutations

    // creates PENDING order and kicks off the Saga
    @Transactional
    public TravelOrderResponseDTO createTravelOrder(TravelOrderRequestDTO dto) {
        TravelOrder travelOrder = travelOrderMapper.toEntity(dto);
        TravelOrder saved = travelOrderRepository.save(travelOrder);

        List<TravelOrderDriver> driverLinks = buildDriverLinks(saved, dto);
        if (!driverLinks.isEmpty()) {
            travelOrderDriverRepository.saveAll(driverLinks);
        }

        eventProducer.publishTravelOrderRequested(
                TravelOrderRequestedEvent.builder()
                        .travelOrderId(saved.getIdTravelOrder())
                        .vehicleId(dto.getVehicleId())
                        .driverIds(dto.getDriverIds())
                        .dateFrom(dto.getDateFrom())
                        .dateTo(dto.getDateTo())
                        .workOrderNumber(dto.getWorkOrderNumber())
                        .createdByUserId(dto.getCreatedByUserId())
                        .occurredAt(Instant.now())
                        .build());

        log.info("Travel order {} created (PENDING) — Saga initiated for vehicle={}, drivers={}",
                saved.getIdTravelOrder(), dto.getVehicleId(), dto.getDriverIds());

        return travelOrderMapper.toResponseDTO(saved);
    }

    @Transactional
    public TravelOrderResponseDTO completeTravelOrder(Long id, CompleteOrderRequestDTO dto) {
        TravelOrder travelOrder = travelOrderRepository.findByIdWithDrivers(id)
                .orElseThrow(() -> new EntityNotFoundException("TravelOrder", id));

        // Status guard
        String currentStatus = travelOrder.getStatus();
        if (!"CONFIRMED".equals(currentStatus) && !"IN_PROGRESS".equals(currentStatus)) {
            throw new IllegalStateException(
                    "Travel order cannot be completed from status: " + currentStatus
                            + ". Must be CONFIRMED or IN_PROGRESS.");
        }

        // Mileage guard
        Integer startingMileage = travelOrder.getStartingMileage();
        Integer endingMileage = dto.endingMileage();

        if (startingMileage == null) {
            throw new IllegalStateException(
                    "Travel order " + id + " has no startingMileage; it may have been created before T1 fix.");
        }
        if (endingMileage <= startingMileage) {
            throw new IllegalArgumentException(
                    "endingMileage (" + endingMileage + ") must be greater than "
                            + "startingMileage (" + startingMileage + ")");
        }

        // Apply changes — @PreUpdate on TravelOrder sets updatedAt automatically
        travelOrder.setEndingMileage(endingMileage);
        travelOrder.setStatus("COMPLETED");

        TravelOrder saved = travelOrderRepository.save(travelOrder);

        // Collect driver IDs from the eagerly loaded join table
        List<Long> driverIds = saved.getDrivers().stream()
                .map(TravelOrderDriver::getDriverId)
                .toList();

        // locationId: TravelOrder stores Integer; event expects Long
        Long locationId = saved.getLocationId() != null
                ? saved.getLocationId().longValue()
                : null;

        // distanceKm computed here so consumers don't need to recalculate
        Integer distanceKm = endingMileage - startingMileage;

        TravelOrderCompletedEvent event = TravelOrderCompletedEvent.builder()
                .travelOrderId(saved.getIdTravelOrder())
                .travelOrderNumber(saved.getTravelOrderNumber())
                .vehicleId(saved.getVehicleId())
                .vehicleSapNumber(saved.getVehicleSapNumber())
                .driverIds(driverIds)
                .startingMileage(startingMileage)
                .endingMileage(endingMileage)
                .distanceKm(distanceKm)
                .locationId(locationId)
                .occurredAt(Instant.now())
                .build();

        eventProducer.publishTravelOrderCompleted(event);

        log.info("Travel order {} → COMPLETED | endingMileage={} distanceKm={}",
                saved.getIdTravelOrder(), endingMileage, distanceKm);

        return travelOrderMapper.toResponseDTO(saved);
    }

    //  Saga callbacks (called by TravelOrderSagaConsumer)

    /**
     * Called when DriversReservedEvent is received — Saga completed successfully.
     * Transitions TravelOrder from PENDING to CONFIRMED.
     */
    @Transactional
    public void confirmTravelOrder(Long travelOrderId) {
        TravelOrder travelOrder = travelOrderRepository.findById(travelOrderId)
                .orElseThrow(() -> new EntityNotFoundException("TravelOrder", travelOrderId));

        if (!"PENDING".equals(travelOrder.getStatus())) {
            log.warn("confirmTravelOrder skipped — order {} already in status {}",
                    travelOrderId, travelOrder.getStatus());
            return;
        }

        travelOrder.setStatus("CONFIRMED");
        travelOrder.setSagaStatus("RESOURCES_RESERVED");
        travelOrderRepository.save(travelOrder);

        log.info("Travel order {} → CONFIRMED (Saga complete)", travelOrderId);
    }

    /**
     * Called when VehicleReservationFailedEvent or DriverReservationFailedEvent
     * is received — Saga failed (with or without compensation).
     */
    @Transactional
    public void failTravelOrder(Long travelOrderId, String reason) {
        TravelOrder travelOrder = travelOrderRepository.findById(travelOrderId)
                .orElseThrow(() -> new EntityNotFoundException("TravelOrder", travelOrderId));

        if (!"PENDING".equals(travelOrder.getStatus())) {
            log.warn("failTravelOrder skipped — order {} already in status {}",
                    travelOrderId, travelOrder.getStatus());
            return;
        }

        travelOrder.setStatus("FAILED");
        travelOrder.setSagaStatus("FAILED");
        travelOrder.setFailureReason(reason);
        travelOrderRepository.save(travelOrder);

        log.warn("Travel order {} → FAILED: {}", travelOrderId, reason);
    }

    /**
     * Called when VehicleReservedEvent is received (optional intermediate step).
     * Updates the Saga status to show vehicle reservation succeeded.
     */
    @Transactional
    public void markVehicleReserved(Long travelOrderId, Integer vehicleSapNumber) {
        travelOrderRepository.findById(travelOrderId).ifPresent(order -> {
            if (order.getVehicleSapNumber() == null) {
                order.setVehicleSapNumber(vehicleSapNumber);
            }
            if ("INITIATED".equals(order.getSagaStatus())) {
                order.setSagaStatus("VEHICLE_RESERVED");
            }
            travelOrderRepository.save(order);
        });
    }

    // Helpers

    private List<TravelOrderDriver> buildDriverLinks(TravelOrder saved, TravelOrderRequestDTO dto) {
        if (dto.getDriverIds() == null || dto.getDriverIds().isEmpty()) {
            return List.of();
        }
        Long primaryId = dto.getPrimaryDriverId() != null
                ? dto.getPrimaryDriverId()
                : dto.getDriverIds().get(0);

        return dto.getDriverIds().stream()
                .map(driverId -> TravelOrderDriver.builder()
                        .travelOrder(saved)
                        .driverId(driverId)
                        .isPrimary(driverId.equals(primaryId))
                        .build())
                .collect(Collectors.toList());
    }
}