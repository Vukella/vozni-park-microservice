package com.voznipark.fleet.mapper;

import com.voznipark.fleet.dto.request.VehicleRequestDTO;
import com.voznipark.fleet.dto.response.VehicleResponseDTO;
import com.voznipark.fleet.dto.summary.*;
import com.voznipark.fleet.entity.*;
import org.springframework.stereotype.Component;

// status and decommission fields are never written from request DTOs
@Component
public class VehicleMapper {

    public Vehicle toEntity(VehicleRequestDTO dto) {
        return Vehicle.builder()
                .sapNumber(dto.getSapNumber())
                .chassisNumber(dto.getChassisNumber())
                .engineNumber(dto.getEngineNumber())
                .tagSerialNumber(dto.getTagSerialNumber())
                .yearOfManufacture(dto.getYearOfManufacture())
                .engineDisplacement(dto.getEngineDisplacement())
                .power(dto.getPower())
                .mass(dto.getMass())
                .color(dto.getColor())
                .loadCapacity(dto.getLoadCapacity())
                .totalMass(dto.getTotalMass())
                .numberOfSeats(dto.getNumberOfSeats())
                .firstRegistrationDate(dto.getFirstRegistrationDate())
                .operationalLeasing(dto.getOperationalLeasing())
                .note(dto.getNote())
                .decommissioned(false)
                .build();
    }

    // does not touch idVehicle, vehicleStatus, statusCode, decommissioned or decommissionDate
    public void updateEntity(Vehicle existing, VehicleRequestDTO dto) {
        existing.setSapNumber(dto.getSapNumber());
        existing.setChassisNumber(dto.getChassisNumber());
        existing.setEngineNumber(dto.getEngineNumber());
        existing.setTagSerialNumber(dto.getTagSerialNumber());
        existing.setYearOfManufacture(dto.getYearOfManufacture());
        existing.setEngineDisplacement(dto.getEngineDisplacement());
        existing.setPower(dto.getPower());
        existing.setMass(dto.getMass());
        existing.setColor(dto.getColor());
        existing.setLoadCapacity(dto.getLoadCapacity());
        existing.setTotalMass(dto.getTotalMass());
        existing.setNumberOfSeats(dto.getNumberOfSeats());
        existing.setFirstRegistrationDate(dto.getFirstRegistrationDate());
        existing.setOperationalLeasing(dto.getOperationalLeasing());
        existing.setNote(dto.getNote());
    }

    public VehicleResponseDTO toResponseDTO(Vehicle v) {
        return VehicleResponseDTO.builder()
                .idVehicle(v.getIdVehicle())
                .sapNumber(v.getSapNumber())
                .chassisNumber(v.getChassisNumber())
                .engineNumber(v.getEngineNumber())
                .tagSerialNumber(v.getTagSerialNumber())
                .yearOfManufacture(v.getYearOfManufacture())
                .engineDisplacement(v.getEngineDisplacement())
                .power(v.getPower())
                .vehicleStatus(v.getVehicleStatus())
                .statusCode(v.getStatusCode())
                .mass(v.getMass())
                .color(v.getColor())
                .loadCapacity(v.getLoadCapacity())
                .totalMass(v.getTotalMass())
                .numberOfSeats(v.getNumberOfSeats())
                .firstRegistrationDate(v.getFirstRegistrationDate())
                .operationalLeasing(v.getOperationalLeasing())
                .decommissioned(v.getDecommissioned())
                .decommissionDate(v.getDecommissionDate())
                .note(v.getNote())
                .registration(toSummary(v.getRegistration()))
                .firstAidKit(toSummary(v.getFirstAidKit()))
                .tachographDevice(toSummary(v.getTachographDevice()))
                .cascoInsurance(toSummary(v.getCascoInsurance()))
                .fireExtinguisher(toSummary(v.getFireExtinguisher()))
                .fuelType(toSummary(v.getFuelType()))
                .vehicleModel(toSummary(v.getVehicleModel()))
                .vehicleType(toSummary(v.getVehicleType()))
                .vehicleSubtype(toSummary(v.getVehicleSubtype()))
                .nisCard(toSummary(v.getNisCard()))
                .tireMarking(toSummary(v.getTireMarking()))
                .build();
    }

    private RegistrationSummaryDTO toSummary(Registration r) {
        if (r == null) return null;
        return RegistrationSummaryDTO.builder()
                .idRegistration(r.getIdRegistration())
                .registrationNumber(r.getRegistrationNumber())
                .dateFrom(r.getDateFrom())
                .dateTo(r.getDateTo())
                .policyNumber(r.getPolicyNumber())
                .status(r.getStatus())
                .statusCode(r.getStatusCode())
                .build();
    }

    private FirstAidKitSummaryDTO toSummary(FirstAidKit f) {
        if (f == null) return null;
        return FirstAidKitSummaryDTO.builder()
                .idFirstAidKit(f.getIdFirstAidKit())
                .expiryDate(f.getExpiryDate())
                .status(f.getStatus())
                .statusCode(f.getStatusCode())
                .build();
    }

    private TachographDeviceSummaryDTO toSummary(TachographDevice t) {
        if (t == null) return null;
        return TachographDeviceSummaryDTO.builder()
                .idTachographDevice(t.getIdTachographDevice())
                .deviceType(t.getDeviceType())
                .serialNumber(t.getSerialNumber())
                .dateFrom(t.getDateFrom())
                .dateTo(t.getDateTo())
                .status(t.getStatus())
                .statusCode(t.getStatusCode())
                .build();
    }

    private CascoInsuranceSummaryDTO toSummary(CascoInsurance c) {
        if (c == null) return null;
        return CascoInsuranceSummaryDTO.builder()
                .idCascoInsurance(c.getIdCascoInsurance())
                .contractNumber(c.getContractNumber())
                .policyNumber(c.getPolicyNumber())
                .dateFrom(c.getDateFrom())
                .dateTo(c.getDateTo())
                .status(c.getStatus())
                .statusCode(c.getStatusCode())
                .build();
    }

    private FireExtinguisherSummaryDTO toSummary(FireExtinguisher f) {
        if (f == null) return null;
        return FireExtinguisherSummaryDTO.builder()
                .idFireExtinguisher(f.getIdFireExtinguisher())
                .serialNumber(f.getSerialNumber())
                .dateFrom(f.getDateFrom())
                .dateTo(f.getDateTo())
                .status(f.getStatus())
                .statusCode(f.getStatusCode())
                .build();
    }

    private FuelTypeSummaryDTO toSummary(FuelType f) {
        if (f == null) return null;
        return FuelTypeSummaryDTO.builder()
                .idFuelType(f.getIdFuelType())
                .name(f.getName())
                .build();
    }

    private BrandSummaryDTO toSummary(Brand b) {
        if (b == null) return null;
        return BrandSummaryDTO.builder()
                .idBrand(b.getIdBrand())
                .name(b.getName())
                .build();
    }

    private VehicleModelSummaryDTO toSummary(VehicleModel m) {
        if (m == null) return null;
        return VehicleModelSummaryDTO.builder()
                .idVehicleModel(m.getIdVehicleModel())
                .name(m.getName())
                .brand(toSummary(m.getBrand()))
                .build();
    }

    private VehicleTypeSummaryDTO toSummary(VehicleType t) {
        if (t == null) return null;
        return VehicleTypeSummaryDTO.builder()
                .idVehicleType(t.getIdVehicleType())
                .name(t.getName())
                .build();
    }

    private VehicleSubtypeSummaryDTO toSummary(VehicleSubtype s) {
        if (s == null) return null;
        return VehicleSubtypeSummaryDTO.builder()
                .idVehicleSubtype(s.getIdVehicleSubtype())
                .name(s.getName())
                .vehicleType(toSummary(s.getVehicleType()))
                .build();
    }

    private NisCardSummaryDTO toSummary(NisCard n) {
        if (n == null) return null;
        return NisCardSummaryDTO.builder()
                .idNisCard(n.getIdNisCard())
                .cardNumber(n.getCardNumber())
                .build();
    }

    private TireMarkingSummaryDTO toSummary(TireMarking t) {
        if (t == null) return null;
        return TireMarkingSummaryDTO.builder()
                .idTireMarking(t.getIdTireMarking())
                .name(t.getName())
                .marking(t.getMarking())
                .build();
    }
}