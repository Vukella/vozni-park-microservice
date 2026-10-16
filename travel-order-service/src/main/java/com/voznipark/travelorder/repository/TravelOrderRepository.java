package com.voznipark.travelorder.repository;

import com.voznipark.travelorder.entity.TravelOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

import java.util.List;
import java.util.Optional;

public interface TravelOrderRepository extends JpaRepository<TravelOrder, Long> {

    @Query("SELECT t FROM TravelOrder t LEFT JOIN FETCH t.drivers WHERE t.idTravelOrder = :id")
    Optional<TravelOrder> findByIdWithDrivers(@Param("id") Long id);

    List<TravelOrder> findByStatus(String status);

    List<TravelOrder> findBySagaStatus(String sagaStatus);

    List<TravelOrder> findByVehicleId(Long vehicleId);

    @Modifying
    @Query("UPDATE TravelOrder t SET t.vehicleSapNumber = :vehicleSapNumber, " +
            "t.sagaStatus = 'VEHICLE_RESERVED', t.updatedAt = :now " +
            "WHERE t.idTravelOrder = :travelOrderId AND t.sagaStatus = 'INITIATED'")
    int markVehicleReservedAtomic(@Param("travelOrderId") Long travelOrderId,
                                  @Param("vehicleSapNumber") Integer vehicleSapNumber,
                                  @Param("now") Instant now);

    @Modifying
    @Query("UPDATE TravelOrder t SET t.status = 'CONFIRMED', " +
            "t.sagaStatus = 'RESOURCES_RESERVED', t.updatedAt = :now " +
            "WHERE t.idTravelOrder = :travelOrderId AND t.status = 'PENDING'")
    int confirmTravelOrderAtomic(@Param("travelOrderId") Long travelOrderId,
                                 @Param("now") Instant now);
}
