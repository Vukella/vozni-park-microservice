package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.TachographDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TachographDeviceRepository extends JpaRepository<TachographDevice, Long> {
}
