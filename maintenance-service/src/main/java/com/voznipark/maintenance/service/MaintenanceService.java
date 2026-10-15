package com.voznipark.maintenance.service;

import com.voznipark.maintenance.dto.ServiceRecordResponseDTO;
import com.voznipark.maintenance.dto.VehicleMileageTrackerResponseDTO;
import com.voznipark.maintenance.entity.ServiceRecord;
import com.voznipark.maintenance.entity.VehicleMileageTracker;
import com.voznipark.maintenance.exception.EntityNotFoundException;
import com.voznipark.maintenance.kafka.event.TravelOrderCompletedEvent;
import com.voznipark.maintenance.kafka.event.VehicleFaultReportedEvent;
import com.voznipark.maintenance.kafka.event.VehicleServiceScheduledEvent;
import com.voznipark.maintenance.mapper.ServiceRecordMapper;
import com.voznipark.maintenance.mapper.VehicleMileageTrackerMapper;
import com.voznipark.maintenance.repository.ServiceRecordRepository;
import com.voznipark.maintenance.repository.VehicleMileageTrackerRepository;
import com.voznipark.maintenance.entity.ProcessedTravelOrderEvent;
import com.voznipark.maintenance.repository.ProcessedTravelOrderEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final VehicleMileageTrackerRepository mileageTrackerRepository;
    private final ServiceRecordMapper serviceRecordMapper;
    private final VehicleMileageTrackerMapper mileageTrackerMapper;
    private final ProcessedTravelOrderEventRepository processedEventRepository;

    @Value("${maintenance.mileage.minor-service-interval-km}")
    private int minorServiceIntervalKm;

    @Value("${maintenance.mileage.major-service-interval-km}")
    private int majorServiceIntervalKm;

    // REST: ServiceRecord

    public List<ServiceRecordResponseDTO> getAllServiceRecords() {
        return serviceRecordRepository.findAll().stream()
                .map(serviceRecordMapper::toDTO)
                .toList();
    }

    public ServiceRecordResponseDTO getServiceRecordById(Long id) {
        return serviceRecordRepository.findById(id)
                .map(serviceRecordMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ServiceRecord not found with id: " + id));
    }

    public List<ServiceRecordResponseDTO> getServiceRecordsByVehicle(Long vehicleId) {
        return serviceRecordRepository.findByVehicleId(vehicleId).stream()
                .map(serviceRecordMapper::toDTO)
                .toList();
    }

    public List<ServiceRecordResponseDTO> getServiceRecordsByStatus(String status) {
        return serviceRecordRepository.findByStatus(status).stream()
                .map(serviceRecordMapper::toDTO)
                .toList();
    }

    public List<ServiceRecordResponseDTO> getServiceRecordsByTriggerType(String triggerType) {
        return serviceRecordRepository.findByTriggerType(triggerType).stream()
                .map(serviceRecordMapper::toDTO)
                .toList();
    }

    @Transactional
    public ServiceRecordResponseDTO updateServiceRecordStatus(Long id, String newStatus) {
        ServiceRecord record = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ServiceRecord not found with id: " + id));
        record.setStatus(newStatus);
        return serviceRecordMapper.toDTO(serviceRecordRepository.save(record));
    }

    // REST: VehicleMileageTracker

    public List<VehicleMileageTrackerResponseDTO> getAllMileageTrackers() {
        return mileageTrackerRepository.findAll().stream()
                .map(mileageTrackerMapper::toDTO)
                .toList();
    }

    public VehicleMileageTrackerResponseDTO getMileageTrackerByVehicle(Long vehicleId) {
        return mileageTrackerRepository.findById(vehicleId)
                .map(mileageTrackerMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "VehicleMileageTracker not found for vehicleId: " + vehicleId));
    }

    // Kafka: trigger path 1 — SCHEDULED

    @Transactional
    public void createServiceRecordFromScheduled(VehicleServiceScheduledEvent event) {
        Instant scheduledAt = event.getScheduledDate() != null
                ? event.getScheduledDate().atStartOfDay(ZoneOffset.UTC).toInstant()
                : Instant.now();

        ServiceRecord record = ServiceRecord.builder()
                .vehicleId(event.getVehicleId())
                .vehicleSapNumber(event.getVehicleSapNumber())
                .serviceType(event.getServiceType())   // MINOR or MAJOR from fleet-service
                .triggerType("SCHEDULED")
                .status("PENDING")
                .scheduledAt(scheduledAt)
                .build();

        serviceRecordRepository.save(record);
        log.info("ServiceRecord created [SCHEDULED] | vehicleId={} serviceType={} scheduledAt={}",
                event.getVehicleId(), event.getServiceType(), scheduledAt);
    }

    // Kafka: trigger path 2 — FAULT_REPAIR

    @Transactional
    public void createServiceRecordFromFault(VehicleFaultReportedEvent event) {
        ServiceRecord record = ServiceRecord.builder()
                .vehicleId(event.getVehicleId())
                .vehicleSapNumber(event.getVehicleSapNumber())
                .serviceType("FAULT_REPAIR")
                .triggerType("FAULT_REPAIR")
                .status("PENDING")
                .description(event.getDescription())
                .sourceEventReference(event.getFaultId())
                .scheduledAt(Instant.now())
                .build();

        serviceRecordRepository.save(record);
        log.info("ServiceRecord created [FAULT_REPAIR] | vehicleId={} faultId={}",
                event.getVehicleId(), event.getFaultId());
    }

    // Kafka: trigger path 3 — MILEAGE

    // accumulates distanceKm per vehicle, checks service thresholds, deduplicates via processed_travel_order_event
    @Transactional
    public void processTravelOrderCompleted(TravelOrderCompletedEvent event) {
        Long vehicleId = event.getVehicleId();
        Long travelOrderId = event.getTravelOrderId();
        int distanceKm = event.getDistanceKm() != null ? event.getDistanceKm() : 0;

        // Step 1 — load or initialise tracker
        VehicleMileageTracker tracker = mileageTrackerRepository.findById(vehicleId)
                .orElseGet(() -> VehicleMileageTracker.builder()
                        .vehicleId(vehicleId)
                        .vehicleSapNumber(event.getVehicleSapNumber())
                        .totalMileageKm(0)
                        .kmSinceLastMinorService(0)
                        .kmSinceLastMajorService(0)
                        .build());

        // Step 2 — deduplication guard
        if (processedEventRepository.existsByTravelOrderId(travelOrderId)) {
            log.warn("Duplicate TravelOrderCompleted skipped | travelOrderId={} vehicleId={}",
                    travelOrderId, vehicleId);
            return;
        }

        // Step 3 — accumulate mileage
        tracker.setTotalMileageKm(tracker.getTotalMileageKm() + distanceKm);
        tracker.setKmSinceLastMinorService(tracker.getKmSinceLastMinorService() + distanceKm);
        tracker.setKmSinceLastMajorService(tracker.getKmSinceLastMajorService() + distanceKm);
        tracker.setLastTravelOrderId(travelOrderId);
        tracker.setVehicleSapNumber(event.getVehicleSapNumber());
        tracker.setLastUpdated(Instant.now());

        // Capture updated counter values before any reset
        int kmMajor = tracker.getKmSinceLastMajorService();
        int kmMinor = tracker.getKmSinceLastMinorService();

        // Step 4 — threshold checks; MAJOR subsumes MINOR
        if (kmMajor >= majorServiceIntervalKm) {
            ServiceRecord record = ServiceRecord.builder()
                    .vehicleId(vehicleId)
                    .vehicleSapNumber(event.getVehicleSapNumber())
                    .serviceType("MAJOR")
                    .triggerType("MILEAGE")
                    .status("PENDING")
                    .sourceEventReference(travelOrderId)
                    .description("Mileage threshold reached: " + kmMajor
                            + " km since last major service (threshold: "
                            + majorServiceIntervalKm + " km)")
                    .scheduledAt(Instant.now())
                    .build();
            serviceRecordRepository.save(record);

            // MAJOR resets both counters — major service covers minor
            tracker.setKmSinceLastMinorService(0);
            tracker.setKmSinceLastMajorService(0);
            log.info("MAJOR ServiceRecord triggered [MILEAGE] | vehicleId={} kmSinceLastMajor={}",
                    vehicleId, kmMajor);

        } else if (kmMinor >= minorServiceIntervalKm) {
            ServiceRecord record = ServiceRecord.builder()
                    .vehicleId(vehicleId)
                    .vehicleSapNumber(event.getVehicleSapNumber())
                    .serviceType("MINOR")
                    .triggerType("MILEAGE")
                    .status("PENDING")
                    .sourceEventReference(travelOrderId)
                    .description("Mileage threshold reached: " + kmMinor
                            + " km since last minor service (threshold: "
                            + minorServiceIntervalKm + " km)")
                    .scheduledAt(Instant.now())
                    .build();
            serviceRecordRepository.save(record);

            // MINOR resets only the minor counter; major counter keeps accumulating
            tracker.setKmSinceLastMinorService(0);
            log.info("MINOR ServiceRecord triggered [MILEAGE] | vehicleId={} kmSinceLastMinor={}",
                    vehicleId, kmMinor);
        }

        // Step 5 — persist tracker (merge handles both INSERT and UPDATE paths)
        mileageTrackerRepository.save(tracker);
        processedEventRepository.save(ProcessedTravelOrderEvent.builder()
                .travelOrderId(travelOrderId)
                .processedAt(Instant.now())
                .build());
        log.debug("VehicleMileageTracker updated | vehicleId={} totalKm={} minorKm={} majorKm={}",
                vehicleId, tracker.getTotalMileageKm(),
                tracker.getKmSinceLastMinorService(), tracker.getKmSinceLastMajorService());
    }
}
