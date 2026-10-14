package com.voznipark.travelorder.repository;

import com.voznipark.travelorder.entity.TravelOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TravelOrderRepository extends JpaRepository<TravelOrder, Long> {

    @Query("SELECT t FROM TravelOrder t LEFT JOIN FETCH t.drivers WHERE t.idTravelOrder = :id")
    Optional<TravelOrder> findByIdWithDrivers(@Param("id") Long id);

    List<TravelOrder> findByStatus(String status);

    List<TravelOrder> findBySagaStatus(String sagaStatus);

    List<TravelOrder> findByVehicleId(Long vehicleId);
}
