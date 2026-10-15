package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.ResourceReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceReservationRepository extends JpaRepository<ResourceReservation, Long> {

    boolean existsByResourceTypeAndResourceIdAndReleasedAtIsNull(
            String resourceType, Long resourceId);

    boolean existsByTravelOrderIdAndResourceTypeAndResourceIdAndReleasedAtIsNull(
            Long travelOrderId, String resourceType, Long resourceId);

    List<ResourceReservation> findByTravelOrderIdAndReleasedAtIsNull(Long travelOrderId);

    List<ResourceReservation> findByTravelOrderIdAndResourceTypeAndReleasedAtIsNull(
            Long travelOrderId, String resourceType);
}