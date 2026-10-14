package com.voznipark.fleet.mapper;

import com.voznipark.fleet.dto.request.DriverRequestDTO;
import com.voznipark.fleet.dto.response.DriverResponseDTO;
import com.voznipark.fleet.dto.summary.DriverSummaryDTO;
import com.voznipark.fleet.entity.Driver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DriverMapper {

    public Driver toEntity(DriverRequestDTO dto) {
        return Driver.builder()
                .sapNumber(dto.sapNumber())
                .fullName(dto.fullName())
                .phone(dto.phone())
                .employeeId(dto.employeeId())
                .licenseRevoked(dto.licenseRevoked())
                .build();
    }

    public void updateEntity(Driver driver, DriverRequestDTO dto) {
        driver.setSapNumber(dto.sapNumber());
        driver.setFullName(dto.fullName());
        driver.setPhone(dto.phone());
        driver.setEmployeeId(dto.employeeId());
        driver.setLicenseRevoked(dto.licenseRevoked());
    }

    public DriverResponseDTO toResponseDTO(Driver d) {
        return new DriverResponseDTO(
                d.getIdDriver(),
                d.getSapNumber(),
                d.getFullName(),
                d.getPhone(),
                d.getStatus(),
                d.getStatusCode(),
                d.getEmployeeId(),
                d.getLicenseRevoked(),
                d.getMedicalCertificate() != null
                        ? d.getMedicalCertificate().getIdMedicalCertificate() : null,
                d.getDriverPermit() != null
                        ? d.getDriverPermit().getIdDriverPermit() : null
        );
    }

    public List<DriverResponseDTO> toResponseDTOList(List<Driver> drivers) {
        return drivers.stream().map(this::toResponseDTO).toList();
    }

    public DriverSummaryDTO toSummaryDTO(Driver d) {
        return new DriverSummaryDTO(
                d.getIdDriver(),
                d.getSapNumber(),
                d.getFullName(),
                d.getStatus(),
                d.getStatusCode()
        );
    }

    public List<DriverSummaryDTO> toSummaryDTOList(List<Driver> drivers) {
        return drivers.stream().map(this::toSummaryDTO).toList();
    }
}