package com.voznipark.maintenance.mapper;

import com.voznipark.maintenance.dto.VehicleMileageTrackerResponseDTO;
import com.voznipark.maintenance.entity.VehicleMileageTracker;
import org.springframework.stereotype.Component;

@Component
public class VehicleMileageTrackerMapper {

    public VehicleMileageTrackerResponseDTO toDTO(VehicleMileageTracker entity) {
        return new VehicleMileageTrackerResponseDTO(
                entity.getVehicleId(),
                entity.getVehicleSapNumber(),
                entity.getTotalMileageKm(),
                entity.getKmSinceLastMinorService(),
                entity.getKmSinceLastMajorService(),
                entity.getLastTravelOrderId(),
                entity.getLastUpdated()
        );
    }
}
