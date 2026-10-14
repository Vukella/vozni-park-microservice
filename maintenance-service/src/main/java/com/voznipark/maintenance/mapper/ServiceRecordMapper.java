package com.voznipark.maintenance.mapper;

import com.voznipark.maintenance.dto.ServiceRecordResponseDTO;
import com.voznipark.maintenance.entity.ServiceRecord;
import org.springframework.stereotype.Component;

@Component
public class ServiceRecordMapper {

    public ServiceRecordResponseDTO toDTO(ServiceRecord entity) {
        return new ServiceRecordResponseDTO(
                entity.getId(),
                entity.getVehicleId(),
                entity.getVehicleSapNumber(),
                entity.getServiceType(),
                entity.getTriggerType(),
                entity.getStatus(),
                entity.getDescription(),
                entity.getSourceEventReference(),
                entity.getScheduledAt(),
                entity.getCreatedAt()
        );
    }
}
