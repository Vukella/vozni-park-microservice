package com.voznipark.fleet.mapper;

import com.voznipark.fleet.dto.response.VehicleFaultResponseDTO;
import com.voznipark.fleet.dto.summary.DriverSummaryDTO;
import com.voznipark.fleet.dto.summary.LocationUnitSummaryDTO;
import com.voznipark.fleet.dto.summary.VehicleSummaryDTO;
import com.voznipark.fleet.entity.Driver;
import com.voznipark.fleet.entity.LocationUnit;
import com.voznipark.fleet.entity.Vehicle;
import com.voznipark.fleet.entity.VehicleFault;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VehicleFaultMapper {

    public VehicleFaultResponseDTO toResponseDTO(VehicleFault fault) {
        return new VehicleFaultResponseDTO(
                fault.getIdVehicleFault(),
                toVehicleSummary(fault.getVehicle()),
                toDriverSummary(fault.getReportedByDriver()),
                toDriverSummary(fault.getHandoverDriver()),
                toLocationSummary(fault.getLocation()),
                fault.getReportDate(),
                fault.getResolutionDate(),
                fault.getHandoverDate(),
                fault.getMileageAtFault(),
                fault.getFaultDescription(),
                fault.getVehicleInoperable(),
                fault.getTowRequired(),
                fault.getServiceType(),
                fault.getCreatedByUserId()
        );
    }

    public List<VehicleFaultResponseDTO> toResponseDTOList(List<VehicleFault> faults) {
        return faults.stream().map(this::toResponseDTO).toList();
    }

    // Helpers

    private VehicleSummaryDTO toVehicleSummary(Vehicle v) {
        if (v == null) return null;
        return new VehicleSummaryDTO(
                v.getIdVehicle(),
                v.getSapNumber(),
                v.getChassisNumber(),
                v.getVehicleStatus(),
                v.getStatusCode()
        );
    }

    private DriverSummaryDTO toDriverSummary(Driver d) {
        if (d == null) return null;
        return new DriverSummaryDTO(
                d.getIdDriver(),
                d.getSapNumber(),
                d.getFullName(),
                d.getStatus(),
                d.getStatusCode()
        );
    }

    private LocationUnitSummaryDTO toLocationSummary(LocationUnit lu) {
        if (lu == null) return null;
        return new LocationUnitSummaryDTO(
                lu.getIdLocationUnit(),
                lu.getLocationName(),
                lu.getLocationAddress()
        );
    }
}