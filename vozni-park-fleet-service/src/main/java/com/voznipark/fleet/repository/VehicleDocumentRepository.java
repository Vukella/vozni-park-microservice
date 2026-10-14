package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, Long> {
    List<VehicleDocument> findByVehicle_IdVehicleOrderByUploadDateDesc(Long vehicleId);
    List<VehicleDocument> findByVehicle_IdVehicleAndDocumentType(Long vehicleId, String documentType);
}