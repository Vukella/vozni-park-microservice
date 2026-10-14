package com.voznipark.maintenance.repository;

import com.voznipark.maintenance.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    List<ServiceRecord> findByVehicleId(Long vehicleId);

    List<ServiceRecord> findByStatus(String status);

    List<ServiceRecord> findByTriggerType(String triggerType);
}
