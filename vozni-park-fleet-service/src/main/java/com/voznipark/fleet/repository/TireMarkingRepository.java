package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.TireMarking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TireMarkingRepository extends JpaRepository<TireMarking, Long> {
}
