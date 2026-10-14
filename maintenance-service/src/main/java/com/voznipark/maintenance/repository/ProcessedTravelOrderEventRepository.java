package com.voznipark.maintenance.repository;

import com.voznipark.maintenance.entity.ProcessedTravelOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedTravelOrderEventRepository
        extends JpaRepository<ProcessedTravelOrderEvent, Long> {

    boolean existsByTravelOrderId(Long travelOrderId);
}